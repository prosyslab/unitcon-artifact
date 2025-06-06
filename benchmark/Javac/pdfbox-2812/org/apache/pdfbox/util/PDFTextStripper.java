/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDThreadBead;


/**
 * This class will take a pdf document and strip out all of the text and ignore the
 * formatting and such.  Please note; it is up to clients of this class to verify that
 * a specific user has the correct permissions to extract text from the
 * PDF document.
 * 
 * The basic flow of this process is that we get a document and use a series of 
 * processXXX() functions that work on smaller and smaller chunks of the page.  
 * Eventually, we fully process each page and then print it. 
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * 
 */
public class PDFTextStripper extends PDFStreamEngine
{

    private static final String thisClassName = PDFTextStripper.class.getSimpleName().toLowerCase();

    private static float DEFAULT_INDENT_THRESHOLD = 2.0f;
    private static float DEFAULT_DROP_THRESHOLD = 2.5f;

    private static final boolean useCustomQuicksort;
    
    //enable the ability to set the default indent/drop thresholds
    //with -D system properties:
    //    pdftextstripper.indent
    //    pdftextstripper.drop
    static
    {
        String sdrop = null, sindent = null;
        try
        {
            String prop = thisClassName + ".indent";
            sindent = System.getProperty(prop);
            prop = thisClassName + ".drop";
            sdrop = System.getProperty(prop);
        }
        catch (SecurityException e)
        {
            // PDFBOX-1946 when run in an applet
            // ignore and use default
        }
        if (sindent != null && sindent.length() > 0)
        {
            try
            {
                float f = Float.parseFloat(sindent);
                DEFAULT_INDENT_THRESHOLD = f;
            }
            catch (NumberFormatException nfe)
            {
                //ignore and use default
            }
        }
        if (sdrop != null && sdrop.length() > 0)
        {
            try
            {
                float f = Float.parseFloat(sdrop);
                DEFAULT_DROP_THRESHOLD = f;
            }
            catch (NumberFormatException nfe)
            {
                //ignore and use default
            }
        }
        
        // check if we need to use the custom quicksort algorithm as a 
        // workaround to the transitivity issue of TextPositionComparator:
        // https://issues.apache.org/jira/browse/PDFBOX-1512
        boolean is16orLess = false;
        try
        {
            String version = System.getProperty("java.specification.version");
            StringTokenizer st = new StringTokenizer(version, ".");
            int majorVersion = Integer.parseInt(st.nextToken());
            int minorVersion = 0;
            if (st.hasMoreTokens())
            {
                minorVersion = Integer.parseInt(st.nextToken());
            }
            is16orLess = majorVersion == 1 && minorVersion <= 6;
        }
        catch (SecurityException e)
        {
            // when run in an applet ignore and use default
            // assume 1.7 or higher so that quicksort is used
        }
        catch (NumberFormatException nfe)
        {
            // should never happen, but if it does,
            // assume 1.7 or higher so that quicksort is used
        }
        useCustomQuicksort = !is16orLess;
    }

    /**
     * The platforms line separator.
     */
    protected final String systemLineSeparator = System.getProperty("line.separator"); 

    private String lineSeparator = systemLineSeparator;
    private String pageSeparator = systemLineSeparator;
    private String wordSeparator = " ";
    private String paragraphStart = "";
    private String paragraphEnd = "";
    private String pageStart = "";
    private String pageEnd = pageSeparator;
    private String articleStart = "";
    private String articleEnd = "";

    private int currentPageNo = 0;
    private int startPage = 1;
    private int endPage = Integer.MAX_VALUE;
    private PDOutlineItem startBookmark = null;
    private int startBookmarkPageNumber = -1;
    private PDOutlineItem endBookmark = null;
    private int endBookmarkPageNumber = -1;
    private boolean suppressDuplicateOverlappingText = true;
    private boolean shouldSeparateByBeads = true;
    private boolean sortByPosition = false;
    private boolean addMoreFormatting = false;
    
    private float indentThreshold = DEFAULT_INDENT_THRESHOLD;
    private float dropThreshold = DEFAULT_DROP_THRESHOLD;

    // We will need to estimate where to add spaces.  
    // These are used to help guess. 
    private float spacingTolerance = .5f;
    private float averageCharTolerance = .3f;

    private List<PDRectangle> beadRectangles = null;
    
    /**
     * The charactersByArticle is used to extract text by article divisions.  For example
     * a PDF that has two columns like a newspaper, we want to extract the first column and
     * then the second column.  In this example the PDF would have 2 beads(or articles), one for
     * each column.  The size of the charactersByArticle would be 5, because not all text on the
     * screen will fall into one of the articles.  The five divisions are shown below
     *
     * Text before first article
     * first article text
     * text between first article and second article
     * second article text
     * text after second article
     *
     * Most PDFs won't have any beads, so charactersByArticle will contain a single entry.
     */
    protected Vector<List<TextPosition>> charactersByArticle = new Vector<List<TextPosition>>();

    private Map<String, TreeMap<Float, TreeSet<Float>>> characterListMapping =
        new HashMap<String, TreeMap<Float, TreeSet<Float>>>();

    /**
     * encoding that text will be written in (or null).
     */
    protected String outputEncoding; 

    /**
     * The document to read.
     */
    protected PDDocument document;
    /**
     * The stream to write the output to.
     */
    protected Writer output;

    /**
     * The normalizer is used to remove text ligatures/presentation forms
     * and to correct the direction of right to left text, such as Arabic and Hebrew.
     */
    private TextNormalize normalize = null;

    /**
     * True if we started a paragraph but haven't ended it
     * yet.
     */
    private boolean inParagraph;

    /**
     * Instantiate a new PDFTextStripper object. This object will load
     * properties from PDFTextStripper.properties and will not do
     * anything special to convert the text to a more encoding-specific
     * output.
     *
     * @throws IOException If there is an error loading the properties.
     */
    public PDFTextStripper() throws IOException
    {
        super( ResourceLoader.loadProperties(
                "org/apache/pdfbox/resources/PDFTextStripper.properties", true ) );
        this.outputEncoding = null;
        normalize = new TextNormalize(this.outputEncoding);
    }

    /**
     * Instantiate a new PDFTextStripper object.  Loading all of the operator mappings
     * from the properties object that is passed in.  Does not convert the text
     * to more encoding-specific output.
     *
     * @param props The properties containing the mapping of operators to PDFOperator
     * classes.
     *
     * @throws IOException If there is an error reading the properties.
     */
    public PDFTextStripper( Properties props ) throws IOException
    {
        super( props );
        this.outputEncoding = null;
        normalize = new TextNormalize(this.outputEncoding);
    }
    /**
     * Instantiate a new PDFTextStripper object. This object will load
     * properties from PDFTextStripper.properties and will apply
     * encoding-specific conversions to the output text.
     *
     * @param encoding The encoding that the output will be written in.
     * @throws IOException If there is an error reading the properties.
     */
    public PDFTextStripper( String encoding ) throws IOException
    {
        super( ResourceLoader.loadProperties(
                "org/apache/pdfbox/resources/PDFTextStripper.properties", true ));
        this.outputEncoding = encoding;
        normalize = new TextNormalize(this.outputEncoding);
    }

    /**
     * This will return the text of a document.  See writeText. <br />
     * NOTE: The document must not be encrypted when coming into this method.
     *
     * @param doc The document to get the text from.
     * @return The text of the PDF document.
     * @throws IOException if the doc state is invalid or it is encrypted.
     */
    public String getText( PDDocument doc ) throws IOException
    {
        StringWriter outputStream = new StringWriter();
        writeText( doc, outputStream );
        return outputStream.toString();
    }

    /**
     * @deprecated
     * @see PDFTextStripper#getText( PDDocument )
     * @param doc The document to extract the text from.
     * @return The document text.
     * @throws IOException If there is an error extracting the text.
     */
    public String getText( COSDocument doc ) throws IOException
    {
        return getText( new PDDocument( doc ) );
    }

    /**
     * @deprecated
     * @see PDFTextStripper#writeText( PDDocument, Writer )
     * @param doc The document to extract the text.
     * @param outputStream The stream to write the text to.
     * @throws IOException If there is an error extracting the text.
     */
    public void writeText( COSDocument doc, Writer outputStream ) throws IOException
    {
        writeText( new PDDocument( doc ), outputStream );
    }

    /**
     * {@inheritDoc}
     */
    public void resetEngine()
    {
        super.resetEngine();
        currentPageNo = 0;
        document = null;
        if (charactersByArticle != null)
        {
            charactersByArticle.clear();
        }
        if (characterListMapping != null)
        {
            characterListMapping.clear();
        }
    }
    
    /**
     * This will take a PDDocument and write the text of that document to the print writer.
     *
     * @param doc The document to get the data from.
     * @param outputStream The location to put the text.
     *
     * @throws IOException If the doc is in an invalid state.
     */
    public void writeText( PDDocument doc, Writer outputStream ) throws IOException
    {
        resetEngine();
        document = doc;
        output = outputStream;
        if (getAddMoreFormatting()) 
        {
            paragraphEnd = lineSeparator;
            pageStart = lineSeparator;
            articleStart = lineSeparator;
            articleEnd = lineSeparator;
        }
        startDocument(document);
        processPages( document.getDocumentCatalog().getAllPages() );
        endDocument(document);
    }

    /**
     * This will process all of the pages and the text that is in them.
     *
     * @param pages The pages object in the document.
     *
     * @throws IOException If there is an error parsing the text.
     */
    protected void processPages( List<COSObjectable> pages ) throws IOException
    {
        if( startBookmark != null )
        {
            startBookmarkPageNumber = getPageNumber( startBookmark, pages );
        }
        if( endBookmark != null )
        {
            endBookmarkPageNumber = getPageNumber( endBookmark, pages );
        }

        if( startBookmarkPageNumber == -1 && startBookmark != null &&
                endBookmarkPageNumber == -1 && endBookmark != null &&
                startBookmark.getCOSObject() == endBookmark.getCOSObject() )
        {
            //this is a special case where both the start and end bookmark
            //are the same but point to nothing.  In this case
            //we will not extract any text.
            startBookmarkPageNumber = 0;
            endBookmarkPageNumber = 0;
        }
        Iterator<COSObjectable> pageIter = pages.iterator();
        while( pageIter.hasNext() )
        {
            PDPage nextPage = (PDPage)pageIter.next();
            PDStream contentStream = nextPage.getContents();
            currentPageNo++;
            if( contentStream != null )
            {
                COSStream contents = contentStream.getStream();
                processPage( nextPage, contents );
            }
        }
    }

    private int getPageNumber( PDOutlineItem bookmark, List<COSObjectable> allPages ) throws IOException
    {
        int pageNumber = -1;
        PDPage page = bookmark.findDestinationPage( document );
        if( page != null )
        {
            pageNumber = allPages.indexOf( page )+1;//use one based indexing
        }
        return pageNumber;
    }

    /**
     * This method is available for subclasses of this class.  It will be called before processing
     * of the document start.
     *
     * @param pdf The PDF document that is being processed.
     * @throws IOException If an IO error occurs.
     */
    protected void startDocument(PDDocument pdf) throws IOException
    {
        // no default implementation, but available for subclasses
    }

    /**
     * This method is available for subclasses of this class.  It will be called after processing
     * of the document finishes.
     *
     * @param pdf The PDF document that is being processed.
     * @throws IOException If an IO error occurs.
     */
    protected void endDocument(PDDocument pdf ) throws IOException
    {
        // no default implementation, but available for subclasses
    }

    /**
     * This will process the contents of a page.
     *
     * @param page The page to process.
     * @param content The contents of the page.
     *
     * @throws IOException If there is an error processing the page.
     */
    protected void processPage( PDPage page, COSStream content ) throws IOException
    {
        if( currentPageNo >= startPage && currentPageNo <= endPage &&
                (startBookmarkPageNumber == -1 || currentPageNo >= startBookmarkPageNumber ) &&
                (endBookmarkPageNumber == -1 || currentPageNo <= endBookmarkPageNumber ))
        {
            startPage( page );

            int numberOfArticleSections = 1;
            if (shouldSeparateByBeads)
            {
                fillBeadRectangles(page);
                numberOfArticleSections += beadRectangles.size() * 2;
            }
            int originalSize = charactersByArticle.size();
            charactersByArticle.setSize( numberOfArticleSections );
            for( int i=0; i<numberOfArticleSections; i++ )
            {
                if( numberOfArticleSections < originalSize )
                {
                    ((List<TextPosition>)charactersByArticle.get( i )).clear();
                }
                else
                {
                    charactersByArticle.set( i, new ArrayList<TextPosition>() );
                }
            }
            characterListMapping.clear();
            processStream( page, page.findResources(), content );
            writePage();
            endPage( page );
        }
    }

    private void fillBeadRectangles(PDPage page)
    {
        beadRectangles = new ArrayList<PDRectangle>();
        for (PDThreadBead bead : page.getThreadBeads())
        {
            if (bead == null)
            {
                // can't skip, because of null entry handling in processTextPosition()
                beadRectangles.add(null);
                continue;
            }
            
            PDRectangle rect = bead.getRectangle();
            
            // bead rectangle is in PDF coordinates (y=0 is bottom),
            // glyphs are in image coordinates (y=0 is top),
            // so we must flip
            PDRectangle mediaBox = page.findMediaBox();
            float upperRightY = mediaBox.getUpperRightY() - rect.getLowerLeftY();
            float lowerLeftY = mediaBox.getUpperRightY() - rect.getUpperRightY();
            rect.setLowerLeftY(lowerLeftY);
            rect.setUpperRightY(upperRightY);
            
            // adjust for cropbox
            PDRectangle cropBox = page.findCropBox();
            if (cropBox.getLowerLeftX() != 0 || cropBox.getLowerLeftY() != 0)
            {
                rect.setLowerLeftX(rect.getLowerLeftX() - cropBox.getLowerLeftX());
                rect.setLowerLeftY(rect.getLowerLeftY() - cropBox.getLowerLeftY());
                rect.setUpperRightX(rect.getUpperRightX() - cropBox.getLowerLeftX());
                rect.setUpperRightY(rect.getUpperRightY() - cropBox.getLowerLeftY());
            }
            
            beadRectangles.add(rect);
        }
    }

    /**
     * Start a new article, which is typically defined as a column
     * on a single page (also referred to as a bead).  This assumes
     * that the primary direction of text is left to right.  
     * Default implementation is to do nothing.  Subclasses
     * may provide additional information.
     *
     * @throws IOException If there is any error writing to the stream.
     */
    protected void startArticle() throws IOException
    {
        startArticle(true);
    }

    /**
     * Start a new article, which is typically defined as a column
     * on a single page (also referred to as a bead).  
     * Default implementation is to do nothing.  Subclasses
     * may provide additional information.
     *
     * @param isltr true if primary direction of text is left to right.
     * @throws IOException If there is any error writing to the stream.
     */
    protected void startArticle(boolean isltr) throws IOException
    {
        output.write(getArticleStart());
    }

    /**
     * End an article.  Default implementation is to do nothing.  Subclasses
     * may provide additional information.
     *
     * @throws IOException If there is any error writing to the stream.
     */
    protected void endArticle() throws IOException
    {
        output.write(getArticleEnd());
    }

    /**
     * Start a new page.  Default implementation is to do nothing.  Subclasses
     * may provide additional information.
     *
     * @param page The page we are about to process.
     *
     * @throws IOException If there is any error writing to the stream.
     */
    protected void startPage( PDPage page ) throws IOException
    {
        //default is to do nothing.
    }

    /**
     * End a page.  Default implementation is to do nothing.  Subclasses
     * may provide additional information.
     *
     * @param page The page we are about to process.
     *
     * @throws IOException If there is any error writing to the stream.
     */
    protected void endPage( PDPage page ) throws IOException
    {
        //default is to do nothing
    }

    private static final float ENDOFLASTTEXTX_RESET_VALUE = -1;
    private static final float MAXYFORLINE_RESET_VALUE = -Float.MAX_VALUE;
    private static final float EXPECTEDSTARTOFNEXTWORDX_RESET_VALUE = -Float.MAX_VALUE;
    private static final float MAXHEIGHTFORLINE_RESET_VALUE = -1;
    private static final float MINYTOPFORLINE_RESET_VALUE = Float.MAX_VALUE;
    private static final float LASTWORDSPACING_RESET_VALUE = -1;

    /**
     * This will print the text of the processed page to "output".
     * It will estimate, based on the coordinates of the text, where
     * newlines and word spacings should be placed. The text will be
     * sorted only if that feature was enabled. 
     *
     * @throws IOException If there is an error writing the text.
     */
    protected void writePage() throws IOException
    {
        float maxYForLine = MAXYFORLINE_RESET_VALUE;
        float minYTopForLine = MINYTOPFORLINE_RESET_VALUE;
        float endOfLastTextX = ENDOFLASTTEXTX_RESET_VALUE;
        float lastWordSpacing = LASTWORDSPACING_RESET_VALUE;
        float maxHeightForLine = MAXHEIGHTFORLINE_RESET_VALUE;
        PositionWrapper lastPosition = null;
        PositionWrapper lastLineStartPosition = null;

        boolean startOfPage = true;//flag to indicate start of page
        boolean startOfArticle = true;
        if(charactersByArticle.size() > 0) 
        { 
            writePageStart();
        }

        for( int i = 0; i < charactersByArticle.size(); i++)
        {
            List<TextPosition> textList = charactersByArticle.get( i );
            if( getSortByPosition() )
            {
                TextPositionComparator comparator = new TextPositionComparator();
                // because the TextPositionComparator is not transitive, but 
                // JDK7+ enforces transitivity on comparators, we need to use
                // a custom quicksort implementation (which is slower, unfortunately).
                if(useCustomQuicksort) 
                {
                    QuickSort.sort( textList, comparator );
                } 
                else 
                {
                    Collections.sort( textList, comparator );
                }
            }
            Iterator<TextPosition> textIter = textList.iterator();
            /* Before we can display the text, we need to do some normalizing.
             * Arabic and Hebrew text is right to left and is typically stored
             * in its logical format, which means that the rightmost character is
             * stored first, followed by the second character from the right etc.
             * However, PDF stores the text in presentation form, which is left to
             * right.  We need to do some normalization to convert the PDF data to
             * the proper logical output format.
             *
             * Note that if we did not sort the text, then the output of reversing the
             * text is undefined and can sometimes produce worse output then not trying
             * to reverse the order.  Sorting should be done for these languages.
             * */

            /* First step is to determine if we have any right to left text, and
             * if so, is it dominant. */
            int ltrCnt = 0;
            int rtlCnt = 0;

            while( textIter.hasNext() )
            {
                TextPosition position = (TextPosition)textIter.next();
                String stringValue = position.getCharacter();
                for (int a = 0; a < stringValue.length(); a++)
                {
                    byte dir = Character.getDirectionality(stringValue.charAt(a));
                    if ((dir == Character.DIRECTIONALITY_LEFT_TO_RIGHT ) ||
                            (dir == Character.DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING) ||
                            (dir == Character.DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE ))
                    {
                        ltrCnt++;
                    }
                    else if ((dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT ) ||
                            (dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) ||
                            (dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING) ||
                            (dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE ))
                    {
                        rtlCnt++;
                    }
                }
            }
            // choose the dominant direction
            boolean isRtlDominant = rtlCnt > ltrCnt;

            startArticle(!isRtlDominant);
            startOfArticle = true;
            // we will later use this to skip reordering
            boolean hasRtl = rtlCnt > 0;

            /* Now cycle through to print the text.
             * We queue up a line at a time before we print so that we can convert
             * the line from presentation form to logical form (if needed). 
             */
            List<TextPosition> line = new ArrayList<TextPosition>();

            textIter = textList.iterator();    // start from the beginning again
            /* PDF files don't always store spaces. We will need to guess where we should add
             * spaces based on the distances between TextPositions. Historically, this was done
             * based on the size of the space character provided by the font. In general, this worked
             * but there were cases where it did not work. Calculating the average character width
             * and using that as a metric works better in some cases but fails in some cases where the
             * spacing worked. So we use both. NOTE: Adobe reader also fails on some of these examples.
             */
            //Keeps track of the previous average character width
            float previousAveCharWidth = -1;
            while( textIter.hasNext() )
            {
                TextPosition position = (TextPosition)textIter.next();
                PositionWrapper current = new PositionWrapper(position);
                String characterValue = position.getCharacter();

                //Resets the average character width when we see a change in font
                // or a change in the font size
                if(lastPosition != null && ((position.getFont() != lastPosition.getTextPosition().getFont())
                        || (position.getFontSize() != lastPosition.getTextPosition().getFontSize())))
                {
                    previousAveCharWidth = -1;
                }

                float positionX;
                float positionY;
                float positionWidth;
                float positionHeight;

                /* If we are sorting, then we need to use the text direction
                 * adjusted coordinates, because they were used in the sorting. */
                if (getSortByPosition())
                {
                    positionX = position.getXDirAdj();
                    positionY = position.getYDirAdj();
                    positionWidth = position.getWidthDirAdj();
                    positionHeight = position.getHeightDir();
                }
                else
                {
                    positionX = position.getX();
                    positionY = position.getY();
                    positionWidth = position.getWidth();
                    positionHeight = position.getHeight();
                }

                //The current amount of characters in a word
                int wordCharCount = position.getIndividualWidths().length;

                /* Estimate the expected width of the space based on the
                 * space character with some margin. */
                float wordSpacing = position.getWidthOfSpace();
                float deltaSpace = 0;
                if (wordSpacing == 0 || Float.isNaN(wordSpacing))
                {
                    deltaSpace = Float.MAX_VALUE;
                }
                else
                {
                    if( lastWordSpacing < 0 )
                    {
                        deltaSpace = (wordSpacing * getSpacingTolerance());
                    }
                    else
                    {
                        deltaSpace = (((wordSpacing+lastWordSpacing)/2f)* getSpacingTolerance());
                    }
                }

                /* Estimate the expected width of the space based on the
                 * average character width with some margin. This calculation does not
                 * make a true average (average of averages) but we found that it gave the
                 * best results after numerous experiments. Based on experiments we also found that
                 * .3 worked well. */
                float averageCharWidth = -1;
                if(previousAveCharWidth < 0)
                {
                    averageCharWidth = (positionWidth/wordCharCount);
                }
                else
                {
                    averageCharWidth = (previousAveCharWidth + (positionWidth/wordCharCount))/2f;
                }
                float deltaCharWidth = (averageCharWidth * getAverageCharTolerance());

                //Compares the values obtained by the average method and the wordSpacing method and picks
                //the smaller number.
                float expectedStartOfNextWordX = EXPECTEDSTARTOFNEXTWORDX_RESET_VALUE;
                if(endOfLastTextX != ENDOFLASTTEXTX_RESET_VALUE)
                {
                    if(deltaCharWidth > deltaSpace)
                    {
                        expectedStartOfNextWordX = endOfLastTextX + deltaSpace;
                    }
                    else
                    {
                        expectedStartOfNextWordX = endOfLastTextX + deltaCharWidth;
                    }
                }

                if( lastPosition != null )
                {
                    if(startOfArticle)
                    {
                        lastPosition.setArticleStart();
                        startOfArticle = false;
                    }
                    // RDD - Here we determine whether this text object is on the current
                    // line.  We use the lastBaselineFontSize to handle the superscript
                    // case, and the size of the current font to handle the subscript case.
                    // Text must overlap with the last rendered baseline text by at least
                    // a small amount in order to be considered as being on the same line.

                    /* XXX BC: In theory, this check should really check if the next char is in full range
                     * seen in this line. This is what I tried to do with minYTopForLine, but this caused a lot
                     * of regression test failures.  So, I'm leaving it be for now. */
                    if(!overlap(positionY, positionHeight, maxYForLine, maxHeightForLine))
                    {
                        writeLine(normalize(line,isRtlDominant,hasRtl),isRtlDominant);
                        line.clear();
                        lastLineStartPosition = 
                            handleLineSeparation(current, lastPosition, lastLineStartPosition, maxHeightForLine);
                        endOfLastTextX = ENDOFLASTTEXTX_RESET_VALUE;
                        expectedStartOfNextWordX = EXPECTEDSTARTOFNEXTWORDX_RESET_VALUE;
                        maxYForLine = MAXYFORLINE_RESET_VALUE;
                        maxHeightForLine = MAXHEIGHTFORLINE_RESET_VALUE;
                        minYTopForLine = MINYTOPFORLINE_RESET_VALUE;
                    }
                    //Test if our TextPosition starts after a new word would be expected to start.
                    if (expectedStartOfNextWordX != EXPECTEDSTARTOFNEXTWORDX_RESET_VALUE 
                            && expectedStartOfNextWordX < positionX &&
                            //only bother adding a space if the last character was not a space
                             lastPosition.getTextPosition().getCharacter() != null &&
                            !lastPosition.getTextPosition().getCharacter().endsWith( " " ) )
                    {
                        line.add(WordSeparator.getSeparator());
                    }
                }
                if (positionY >= maxYForLine)
                {
                    maxYForLine = positionY;
                }
                // RDD - endX is what PDF considers to be the x coordinate of the
                // end position of the text.  We use it in computing our metrics below.
                endOfLastTextX = positionX + positionWidth;

                // add it to the list
                if (characterValue != null)
                {
                    if(startOfPage && lastPosition==null)
                    {
                        writeParagraphStart();//not sure this is correct for RTL?
                    }
                    line.add(position);
                }
                maxHeightForLine = Math.max( maxHeightForLine, positionHeight );
                minYTopForLine = Math.min(minYTopForLine,positionY - positionHeight);
                lastPosition = current;
                if(startOfPage)
                {
                    lastPosition.setParagraphStart();
                    lastPosition.setLineStart();
                    lastLineStartPosition = lastPosition;
                    startOfPage=false;
                }
                lastWordSpacing = wordSpacing;
                previousAveCharWidth = averageCharWidth;
            }
            // print the final line
            if (line.size() > 0)
            {
                writeLine(normalize(line,isRtlDominant,hasRtl),isRtlDominant);
                writeParagraphEnd();
            }
            endArticle();
        }
        writePageEnd();
    }

    private boolean overlap( float y1, float height1, float y2, float height2 )
    {
        return within( y1, y2, .1f) || (y2 <= y1 && y2 >= y1-height1) ||
        (y1 <= y2 && y1 >= y2-height2);
    }

    /**
     * Write the page separator value to the output stream.
     * @throws IOException
     *             If there is a problem writing out the pageseparator to the document.
     */
    protected void writePageSeperator() throws IOException
    {
        // RDD - newline at end of flush - required for end of page (so that the top
        // of the next page starts on its own line.
        output.write(getPageSeparator());
        output.flush();
    }

    /**
     * Write the line separator value to the output stream.
     * @throws IOException
     *             If there is a problem writing out the lineseparator to the document.
     */
    protected void writeLineSeparator( ) throws IOException
    {
        output.write(getLineSeparator());
    }


    /**
     * Write the word separator value to the output stream.
     * @throws IOException
     *             If there is a problem writing out the wordseparator to the document.
     */
    protected void writeWordSeparator() throws IOException
    {
        output.write(getWordSeparator());
    }

    /**
     * Write the string in TextPosition to the output stream.
     *
     * @param text The text to write to the stream.
     * @throws IOException If there is an error when writing the text.
     */
    protected void writeCharacters( TextPosition text ) throws IOException
    {
        output.write( text.getCharacter() );
    }

    /**
     * Write a Java string to the output stream. The default implementation will ignore the <code>textPositions</code>
     * and just calls {@link #writeString(String)}.
     *
     * @param text The text to write to the stream.
     * @param textPositions The TextPositions belonging to the text.
     * @throws IOException If there is an error when writing the text.
     */
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException
    {
        writeString(text);
    }

    /**
     * Write a Java string to the output stream.
     *
     * @param text The text to write to the stream.
     * @throws IOException If there is an error when writing the text.
     */
    protected void writeString( String text ) throws IOException
    {
        output.write( text );
    }

    /**
     * This will determine of two floating point numbers are within a specified variance.
     *
     * @param first The first number to compare to.
     * @param second The second number to compare to.
     * @param variance The allowed variance.
     */
    private boolean within( float first, float second, float variance )
    {
        return second < first + variance && second > first - variance;
    }

    /**
     * This will process a TextPosition object and add the
     * text to the list of characters on a page.  It takes care of
     * overlapping text.
     *
     * @param text The text to process.
     */
    protected void processTextPosition( TextPosition text )
    {
        boolean showCharacter = true;
        if( suppressDuplicateOverlappingText )
        {
            showCharacter = false;
            String textCharacter = text.getCharacter();
            float textX = text.getX();
            float textY = text.getY();
            TreeMap<Float, TreeSet<Float>> sameTextCharacters = characterListMapping.get( textCharacter );
            if( sameTextCharacters == null )
            {
                sameTextCharacters = new TreeMap<Float, TreeSet<Float>>();
                characterListMapping.put( textCharacter, sameTextCharacters );
            }
            // RDD - Here we compute the value that represents the end of the rendered
            // text.  This value is used to determine whether subsequent text rendered
            // on the same line overwrites the current text.
            //
            // We subtract any positive padding to handle cases where extreme amounts
            // of padding are applied, then backed off (not sure why this is done, but there
            // are cases where the padding is on the order of 10x the character width, and
            // the TJ just backs up to compensate after each character).  Also, we subtract
            // an amount to allow for kerning (a percentage of the width of the last
            // character).
            //
            boolean suppressCharacter = false;
            float tolerance = (text.getWidth()/textCharacter.length())/3.0f;
            
            SortedMap<Float, TreeSet<Float>> xMatches =
                sameTextCharacters.subMap(textX - tolerance, textX + tolerance );
            for (TreeSet<Float> xMatch : xMatches.values()) 
            {
                SortedSet<Float> yMatches =
                    xMatch.subSet(textY - tolerance , textY + tolerance );
                if (!yMatches.isEmpty()) 
                {
                    suppressCharacter = true;
                    break;
                }
            }
            if( !suppressCharacter )
            {
                TreeSet<Float> ySet = sameTextCharacters.get(textX);
                if (ySet == null) 
                {
                    ySet = new TreeSet<Float>();
                    sameTextCharacters.put( textX,  ySet );
                }
                ySet.add( textY );
                showCharacter = true;
            }
        }
        if( showCharacter )
        {
            //if we are showing the character then we need to determine which
            //article it belongs to.
            int foundArticleDivisionIndex = -1;
            int notFoundButFirstLeftAndAboveArticleDivisionIndex = -1;
            int notFoundButFirstLeftArticleDivisionIndex = -1;
            int notFoundButFirstAboveArticleDivisionIndex = -1;
            float x = text.getX();
            float y = text.getY();
            if (shouldSeparateByBeads)
            {
                for (int i = 0; i < beadRectangles.size() && foundArticleDivisionIndex == -1; i++)
                {
                    PDRectangle rect = beadRectangles.get(i);
                    if( rect != null )
                    {
                        if( rect.contains( x, y ) )
                        {
                            foundArticleDivisionIndex = i*2+1;
                        }
                        else if( (x < rect.getLowerLeftX() ||
                                y < rect.getUpperRightY()) &&
                                notFoundButFirstLeftAndAboveArticleDivisionIndex == -1)
                        {
                            notFoundButFirstLeftAndAboveArticleDivisionIndex = i*2;
                        }
                        else if( x < rect.getLowerLeftX() &&
                                notFoundButFirstLeftArticleDivisionIndex == -1)
                        {
                            notFoundButFirstLeftArticleDivisionIndex = i*2;
                        }
                        else if( y < rect.getUpperRightY() &&
                                notFoundButFirstAboveArticleDivisionIndex == -1)
                        {
                            notFoundButFirstAboveArticleDivisionIndex = i*2;
                        }
                    }
                    else
                    {
                        foundArticleDivisionIndex = 0;
                    }
                }
            }
            else
            {
                foundArticleDivisionIndex = 0;
            }
            int articleDivisionIndex = -1;
            if( foundArticleDivisionIndex != -1 )
            {
                articleDivisionIndex = foundArticleDivisionIndex;
            }
            else if( notFoundButFirstLeftAndAboveArticleDivisionIndex != -1 )
            {
                articleDivisionIndex = notFoundButFirstLeftAndAboveArticleDivisionIndex;
            }
            else if( notFoundButFirstLeftArticleDivisionIndex != -1 )
            {
                articleDivisionIndex = notFoundButFirstLeftArticleDivisionIndex;
            }
            else if( notFoundButFirstAboveArticleDivisionIndex != -1 )
            {
                articleDivisionIndex = notFoundButFirstAboveArticleDivisionIndex;
            }
            else
            {
                articleDivisionIndex = charactersByArticle.size()-1;
            }

            List<TextPosition> textList = (List<TextPosition>) charactersByArticle.get( articleDivisionIndex );

            /* In the wild, some PDF encoded documents put diacritics (accents on
             * top of characters) into a separate Tj element.  When displaying them
             * graphically, the two chunks get overlayed.  With text output though,
             * we need to do the overlay. This code recombines the diacritic with
             * its associated character if the two are consecutive.
             */ 
            if(textList.isEmpty())
            {
                textList.add(text);
            }
            else
            {
                /* test if we overlap the previous entry.  
                 * Note that we are making an assumption that we need to only look back
                 * one TextPosition to find what we are overlapping.  
                 * This may not always be true. */
                TextPosition previousTextPosition = (TextPosition)textList.get(textList.size()-1);
                if(text.isDiacritic() && previousTextPosition.contains(text))
                {
                    previousTextPosition.mergeDiacritic(text, normalize);
                }
                /* If the previous TextPosition was the diacritic, merge it into this
                 * one and remove it from the list. */
                else if(previousTextPosition.isDiacritic() && text.contains(previousTextPosition))
                {
                    text.mergeDiacritic(previousTextPosition, normalize);
                    textList.remove(textList.size()-1);
                    textList.add(text);
                }
                else
                {
                    textList.add(text);
                }
            }
        }
    }

    /**
     * This is the page that the text extraction will start on.  The pages start
     * at page 1.  For example in a 5 page PDF document, if the start page is 1
     * then all pages will be extracted.  If the start page is 4 then pages 4 and 5
     * will be extracted.  The default value is 1.
     *
     * @return Value of property startPage.
     */
    public int getStartPage()
    {
        return startPage;
    }

    /**
     * This will set the first page to be extracted by this class.
     *
     * @param startPageValue New value of 1-based startPage property.
     */
    public void setStartPage(int startPageValue)
    {
        startPage = startPageValue;
    }

    /**
     * This will get the last page that will be extracted.  This is inclusive,
     * for example if a 5 page PDF an endPage value of 5 would extract the
     * entire document, an end page of 2 would extract pages 1 and 2.  This defaults
     * to Integer.MAX_VALUE such that all pages of the pdf will be extracted.
     *
     * @return Value of property endPage.
     */
    public int getEndPage()
    {
        return endPage;
    }

    /**
     * This will set the last page to be extracted by this class.
     *
     * @param endPageValue New value of 1-based endPage property.
     */
    public void setEndPage(int endPageValue)
    {
        endPage = endPageValue;
    }

    /**
     * Set the desired line separator for output text.  The line.separator
     * system property is used if the line separator preference is not set
     * explicitly using this method.
     *
     * @param separator The desired line separator string.
     */
    public void setLineSeparator(String separator)
    {
        lineSeparator = separator;
    }

    /**
     * This will get the line separator.
     *
     * @return The desired line separator string.
     */
    public String getLineSeparator()
    {
        return lineSeparator;
    }

    /**
     * Set the desired page separator for output text.  The line.separator
     * system property is used if the page separator preference is not set
     * explicitly using this method.
     *
     * @param separator The desired page separator string.
     * 
     * @deprecated use {@link #setPageStart(String) and {@link #setPageEnd(String)} instead
     */
    public void setPageSeparator(String separator)
    {
        pageSeparator = separator;
    }

    /**
     * This will get the word separator.
     *
     * @return The desired word separator string.
     */
    public String getWordSeparator()
    {
        return wordSeparator;
    }

    /**
     * Set the desired word separator for output text.  The PDFBox text extraction
     * algorithm will output a space character if there is enough space between
     * two words.  By default a space character is used.  If you need and accurate
     * count of characters that are found in a PDF document then you might want to
     * set the word separator to the empty string.
     *
     * @param separator The desired page separator string.
     */
    public void setWordSeparator(String separator)
    {
        wordSeparator = separator;
    }

    /**
     * This will get the page separator.
     *
     * @return The page separator string.
     * 
     * @deprecated use {@link #getPageStart()} and {@link #getPageEnd()} instead
     */
    public String getPageSeparator()
    {
        return pageSeparator;
    }
    /**
     * @return Returns the suppressDuplicateOverlappingText.
     */
    public boolean getSuppressDuplicateOverlappingText()
    {
        return suppressDuplicateOverlappingText;
    }

    /**
     * Get the current page number that is being processed.
     *
     * @return A 1 based number representing the current page.
     */
    protected int getCurrentPageNo()
    {
        return currentPageNo;
    }

    /**
     * The output stream that is being written to.
     *
     * @return The stream that output is being written to.
     */
    protected Writer getOutput()
    {
        return output;
    }

    /**
     * Character strings are grouped by articles.  It is quite common that there
     * will only be a single article.  This returns a List that contains List objects,
     * the inner lists will contain TextPosition objects.
     *
     * @return A double List of TextPositions for all text strings on the page.
     */
    protected Vector<List<TextPosition>> getCharactersByArticle()
    {
        return charactersByArticle;
    }

    /**
     * By default the text stripper will attempt to remove text that overlapps each other.
     * Word paints the same character several times in order to make it look bold.  By setting
     * this to false all text will be extracted, which means that certain sections will be
     * duplicated, but better performance will be noticed.
     *
     * @param suppressDuplicateOverlappingTextValue The suppressDuplicateOverlappingText to set.
     */
    public void setSuppressDuplicateOverlappingText(
            boolean suppressDuplicateOverlappingTextValue)
    {
        suppressDuplicateOverlappingText = suppressDuplicateOverlappingTextValue;
    }

    /**
     * This will tell if the text stripper should separate by beads.
     *
     * @return If the text will be grouped by beads.
     */
    public boolean getSeparateByBeads()
    {
        return shouldSeparateByBeads;
    }

    /**
     * Set if the text stripper should group the text output by a list of beads.  The default value is true!
     *
     * @param aShouldSeparateByBeads The new grouping of beads.
     */
    public void setShouldSeparateByBeads(boolean aShouldSeparateByBeads)
    {
        shouldSeparateByBeads = aShouldSeparateByBeads;
    }

    /**
     * Get the bookmark where text extraction should end, inclusive.  Default is null.
     *
     * @return The ending bookmark.
     */
    public PDOutlineItem getEndBookmark()
    {
        return endBookmark;
    }

    /**
     * Set the bookmark where the text extraction should stop.
     *
     * @param aEndBookmark The ending bookmark.
     */
    public void setEndBookmark(PDOutlineItem aEndBookmark)
    {
        endBookmark = aEndBookmark;
    }

    /**
     * Get the bookmark where text extraction should start, inclusive.  Default is null.
     *
     * @return The starting bookmark.
     */
    public PDOutlineItem getStartBookmark()
    {
        return startBookmark;
    }

    /**
     * Set the bookmark where text extraction should start, inclusive.
     *
     * @param aStartBookmark The starting bookmark.
     */
    public void setStartBookmark(PDOutlineItem aStartBookmark)
    {
        startBookmark = aStartBookmark;
    }

    /**
     * This will tell if the text stripper should add some more text formatting.
     * @return true if some more text formatting will be added
     */
    public boolean getAddMoreFormatting()
    {
        return addMoreFormatting;
    }
    
    /**
     * There will some additional text formatting be added if addMoreFormatting
     * is set to true. Default is false. 
     * @param newAddMoreFormatting Tell PDFBox to add some more text formatting
     */
    public void setAddMoreFormatting(boolean newAddMoreFormatting)
    {
        addMoreFormatting = newAddMoreFormatting;
    }

    /**
     * This will tell if the text stripper should sort the text tokens
     * before writing to the stream.
     *
     * @return true If the text tokens will be sorted before being written.
     */
    public boolean getSortByPosition()
    {
        return sortByPosition;
    }

    /**
     * The order of the text tokens in a PDF file may not be in the same
     * as they appear visually on the screen.  For example, a PDF writer may
     * write out all text by font, so all bold or larger text, then make a second
     * pass and write out the normal text.<br/>
     * The default is to <b>not</b> sort by position.<br/>
     * <br/>
     * A PDF writer could choose to write each character in a different order.  By
     * default PDFBox does <b>not</b> sort the text tokens before processing them due to
     * performance reasons.
     *
     * @param newSortByPosition Tell PDFBox to sort the text positions.
     */
    public void setSortByPosition(boolean newSortByPosition)
    {
        sortByPosition = newSortByPosition;
    }

    /**
     * Get the current space width-based tolerance value that is being used
     * to estimate where spaces in text should be added.  Note that the
     * default value for this has been determined from trial and error. 
     * 
     * @return The current tolerance / scaling factor
     */
    public float getSpacingTolerance() 
    {
        return spacingTolerance;
    }

    /**
     * Set the space width-based tolerance value that is used
     * to estimate where spaces in text should be added.  Note that the
     * default value for this has been determined from trial and error.
     * Setting this value larger will reduce the number of spaces added. 
     * 
     * @param spacingToleranceValue tolerance / scaling factor to use
     */
    public void setSpacingTolerance(float spacingToleranceValue)
    {
        spacingTolerance = spacingToleranceValue;
    }

    /**
     * Get the current character width-based tolerance value that is being used
     * to estimate where spaces in text should be added.  Note that the
     * default value for this has been determined from trial and error.
     * 
     * @return The current tolerance / scaling factor
     */
    public float getAverageCharTolerance() 
    {
        return averageCharTolerance;
    }

    /**
     * Set the character width-based tolerance value that is used
     * to estimate where spaces in text should be added.  Note that the
     * default value for this has been determined from trial and error.
     * Setting this value larger will reduce the number of spaces added. 
     * 
     * @param averageCharToleranceValue average tolerance / scaling factor to use
     */
    public void setAverageCharTolerance(float averageCharToleranceValue) 
    {
        averageCharTolerance = averageCharToleranceValue;
    }


    /**
     * returns the multiple of whitespace character widths
     * for the current text which the current
     * line start can be indented from the previous line start
     * beyond which the current line start is considered
     * to be a paragraph start.
     * @return the number of whitespace character widths to use
     * when detecting paragraph indents.
     */
    public float getIndentThreshold() 
    {
        return indentThreshold;
    }

    /**
     * sets the multiple of whitespace character widths
     * for the current text which the current
     * line start can be indented from the previous line start
     * beyond which the current line start is considered
     * to be a paragraph start.  The default value is 2.0.
     *
     * @param indentThresholdValue the number of whitespace character widths to use
     * when detecting paragraph indents.
     */
    public void setIndentThreshold(float indentThresholdValue) 
    {
        indentThreshold = indentThresholdValue;
    }

    /**
     * the minimum whitespace, as a multiple
     * of the max height of the current characters
     * beyond which the current line start is considered
     * to be a paragraph start.
     * @return the character height multiple for
     * max allowed whitespace between lines in
     * the same paragraph.
     */
    public float getDropThreshold() 
    {
        return dropThreshold;
    }

    /**
     * sets the minimum whitespace, as a multiple
     * of the max height of the current characters
     * beyond which the current line start is considered
     * to be a paragraph start.  The default value is 2.5.
     *
     * @param dropThresholdValue the character height multiple for
     * max allowed whitespace between lines in
     * the same paragraph.
     */
    public void setDropThreshold(float dropThresholdValue) 
    {
        dropThreshold = dropThresholdValue;
    }

    /**
     * Returns the string which will be used at the beginning of a paragraph.
     * @return the paragraph start string
     */
    public String getParagraphStart()
    {
        return paragraphStart;
    }

    /**
     * Sets the string which will be used at the beginning of a paragraph.
     * @param s the paragraph start string
     */
    public void setParagraphStart(String s)
    {
        paragraphStart = s;
    }

    /**
     * Returns the string which will be used at the end of a paragraph.
     * @return the paragraph end string
     */
    public String getParagraphEnd()
    {
        return paragraphEnd;
    }

    /**
     * Sets the string which will be used at the end of a paragraph.
     * @param s the paragraph end string
     */
    public void setParagraphEnd(String s)
    {
        paragraphEnd = s;
    }


    /**
     * Returns the string which will be used at the beginning of a page.
     * @return the page start string
     */
    public String getPageStart() 
    {
        return pageStart;
    }

    /**
     * Sets the string which will be used at the beginning of a page.
     * @param pageStartValue the page start string
     */
    public void setPageStart(String pageStartValue) 
    {
        pageStart = pageStartValue;
    }

    /**
     * Returns the string which will be used at the end of a page.
     * @return the page end string
     */
    public String getPageEnd() 
    {
        return pageEnd;
    }

    /**
     * Sets the string which will be used at the end of a page.
     * @param pageEndValue the page end string
     */
    public void setPageEnd(String pageEndValue) 
    {
        pageEnd = pageEndValue;
    }

    /**
     * Returns the string which will be used at the beginning of an article.
     * @return the article start string
     */
    public String getArticleStart() 
    {
        return articleStart;
    }

    /**
     * Sets the string which will be used at the beginning of an article.
     * @param articleStartValue the article start string
     */
    public void setArticleStart(String articleStartValue) 
    {
        articleStart = articleStartValue;
    }

    /**
     * Returns the string which will be used at the end of an article.
     * @return the article end string
     */
    public String getArticleEnd()
    {
        return articleEnd;
    }

    /**
     * Sets the string which will be used at the end of an article.
     * @param articleEndValue the article end string
     */
    public void setArticleEnd(String articleEndValue)
    {
        articleEnd = articleEndValue;
    }


    /**
     * Reverse characters of a compound Arabic glyph.
     * When getSortByPosition() is true, inspect the sequence encoded
     * by one glyph. If the glyph encodes two or more Arabic characters,
     * reverse these characters from a logical order to a visual order.
     * This ensures that the bidirectional algorithm that runs later will
     * convert them back to a logical order.
     * 
     * @param str a string obtained from font.encoding()
     * 
     * @return the reversed string
     */
    public String inspectFontEncoding(String str)
    {
        if (!sortByPosition || str == null || str.length() < 2)
        {
            return str;
        }
        for (int i = 0; i < str.length(); ++i)
        {
            if (Character.getDirectionality(str.charAt(i))
                    != Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC)
            {
                return str;
            }
        }
        StringBuilder reversed = new StringBuilder(str.length());
        for (int i = str.length() - 1; i >= 0; --i)
        {
            reversed.append(str.charAt(i));
        }
        return reversed.toString();
    }

    /**
     * handles the line separator for a new line given
     * the specified current and previous TextPositions.
     * @param current the current text position
     * @param lastPosition the previous text position
     * @param lastLineStartPosition the last text position that followed a line
     *        separator.
     * @param maxHeightForLine max height for positions since lastLineStartPosition
     * @return start position of the last line
     * @throws IOException if something went wrong
     */
    protected PositionWrapper handleLineSeparation(PositionWrapper current,
            PositionWrapper lastPosition, PositionWrapper lastLineStartPosition, float maxHeightForLine)
            throws IOException 
            {
        current.setLineStart();
        isParagraphSeparation(current, lastPosition, lastLineStartPosition, maxHeightForLine);
        lastLineStartPosition = current;
        if (current.isParagraphStart())  
        {
            if(lastPosition.isArticleStart()) 
            {
                writeParagraphStart();
            } 
            else 
            {
                writeLineSeparator();
                writeParagraphSeparator();
            }
        } 
        else 
        {
            writeLineSeparator();
        }
        return lastLineStartPosition;
    }
    
    /**
     * tests the relationship between the last text position, the current text
     * position and the last text position that followed a line separator to
     * decide if the gap represents a paragraph separation. This should
     * <i>only</i> be called for consecutive text positions that first pass the
     * line separation test.
     * <p>
     * This base implementation tests to see if the lastLineStartPosition is
     * null OR if the current vertical position has dropped below the last text
     * vertical position by at least 2.5 times the current text height OR if the
     * current horizontal position is indented by at least 2 times the current
     * width of a space character.</p>
     * <p>
     * This also attempts to identify text that is indented under a hanging indent.</p>
     * <p>
     * This method sets the isParagraphStart and isHangingIndent flags on the current
     * position object.</p>
     *
     * @param position the current text position.  This may have its isParagraphStart
     * or isHangingIndent flags set upon return.
     * @param lastPosition the previous text position (should not be null).
     * @param lastLineStartPosition the last text position that followed a line
     *            separator. May be null.
     * @param maxHeightForLine max height for text positions since lasLineStartPosition.
     */
    protected void isParagraphSeparation(PositionWrapper position,  
            PositionWrapper lastPosition, PositionWrapper lastLineStartPosition, float maxHeightForLine)
    {
        boolean result = false;
        if(lastLineStartPosition == null) 
        {
            result = true;
        }
        else
        {
            float yGap = Math.abs(position.getTextPosition().getYDirAdj()-
                    lastPosition.getTextPosition().getYDirAdj());
            float newYVal = multiplyFloat(getDropThreshold(), maxHeightForLine);
            // do we need to flip this for rtl?
            float xGap = position.getTextPosition().getXDirAdj() -
                    lastLineStartPosition.getTextPosition().getXDirAdj();
            float newXVal = multiplyFloat(getIndentThreshold(), position.getTextPosition().getWidthOfSpace());
            float positionWidth = multiplyFloat(0.25f, position.getTextPosition().getWidth());

            if (yGap > newYVal)
            {
                result = true;
            }
            else if (xGap > newXVal)
            {
                //text is indented, but try to screen for hanging indent
                if(!lastLineStartPosition.isParagraphStart())
                {
                     result = true;
                }
                else
                {
                     position.setHangingIndent();
                }
            }
            else if(xGap < -position.getTextPosition().getWidthOfSpace())
            {
                //text is left of previous line. Was it a hanging indent?
                if(!lastLineStartPosition.isParagraphStart())
                {
                            result = true;
                }
            }
            else if (Math.abs(xGap) < positionWidth)
            {
                //current horizontal position is within 1/4 a char of the last
                //linestart.  We'll treat them as lined up.
                if(lastLineStartPosition.isHangingIndent())
                {
                    position.setHangingIndent();
                }
                else if(lastLineStartPosition.isParagraphStart())
                {
                    //check to see if the previous line looks like
                    //any of a number of standard list item formats
                    Pattern liPattern = matchListItemPattern(lastLineStartPosition);
                    if(liPattern!=null)
                    {
                        Pattern currentPattern = matchListItemPattern(position);
                        if(liPattern == currentPattern)
                        {
                            result = true;
                        }
                    }
               }
           }
        }
        if(result)
        {
            position.setParagraphStart();
        }
    }

    private float multiplyFloat(float value1, float value2)
    {
        // multiply 2 floats and truncate the resulting value to 3 decimal places
        // to avoid wrong results when comparing with another float
        return Math.round(value1 * value2 * 1000) / 1000f;
    }
    /**
     * writes the paragraph separator string to the output.
     * @throws IOException if something went wrong
     */
    protected void writeParagraphSeparator()throws IOException
    {
        writeParagraphEnd();
        writeParagraphStart();
    }

    /**
     * Write something (if defined) at the start of a paragraph.
     * @throws IOException if something went wrong
     */
    protected void writeParagraphStart() throws IOException
    {
        if (inParagraph) 
        {
            writeParagraphEnd();
            inParagraph = false;
        }
        output.write(getParagraphStart());
        inParagraph = true;
    }

    /**
     * Write something (if defined) at the end of a paragraph.
     * @throws IOException if something went wrong
     */
    protected void writeParagraphEnd() throws IOException
    {
        if (!inParagraph)
        {
            writeParagraphStart();
        }
        output.write(getParagraphEnd());
        inParagraph = false;
    }

    /**
     * Write something (if defined) at the start of a page.
     * @throws IOException if something went wrong
     */
    protected void writePageStart()throws IOException
    {
        output.write(getPageStart());
    }

    /**
     * Write something (if defined) at the end of a page.
     * @throws IOException if something went wrong
     */
    protected void writePageEnd()throws IOException
    {
        output.write(getPageEnd());
    }

    /**
     * returns the list item Pattern object that matches
     * the text at the specified PositionWrapper or null
     * if the text does not match such a pattern.  The list
     * of Patterns tested against is given by the
     * {@link #getListItemPatterns()} method.  To add to
     * the list, simply override that method (if sub-classing)
     * or explicitly supply your own list using
     * {@link #setListItemPatterns(List)}.
     * @param pw position
     * @return the matching pattern
     */
    protected Pattern matchListItemPattern(PositionWrapper pw) 
    {
        TextPosition tp = pw.getTextPosition();
        String txt = tp.getCharacter();
        return matchPattern(txt,getListItemPatterns());
    }

    /**
     * a list of regular expressions that match commonly used
     * list item formats, i.e. bullets, numbers, letters,
     * Roman numerals, etc.  Not meant to be
     * comprehensive.
     */
    private static final String[] LIST_ITEM_EXPRESSIONS = {
            "\\.",
            "\\d+\\.",
            "\\[\\d+\\]",
            "\\d+\\)",
            "[A-Z]\\.",
            "[a-z]\\.",
            "[A-Z]\\)",
            "[a-z]\\)",
            "[IVXL]+\\.",
            "[ivxl]+\\.",

    };

    private List<Pattern> listOfPatterns = null;
    /**
     * use to supply a different set of regular expression
     * patterns for matching list item starts.
     *
     * @param patterns list of patterns
     */
    protected void setListItemPatterns(List<Pattern> patterns)
    {
        listOfPatterns = patterns;
    }

    /**
     * returns a list of regular expression Patterns representing
     * different common list item formats.  For example
     * numbered items of form:
     * <ol>
     * <li>some text</li>
     * <li>more text</li>
     * </ol>
     * or
     * <ul>
     * <li>some text</li>
     * <li>more text</li>
     * </ul>
     * etc., all begin with some character pattern. The pattern "\\d+\." (matches "1.", "2.", ...)
     * or "\[\\d+\]" (matches "[1]", "[2]", ...).
     * <p>
     * This method returns a list of such regular expression Patterns.
     * @return a list of Pattern objects.
     */
    protected List<Pattern> getListItemPatterns()
    {
        if(listOfPatterns == null)
        {
            listOfPatterns = new ArrayList<Pattern>();
            for(String expression : LIST_ITEM_EXPRESSIONS)
            {
                Pattern p = Pattern.compile(expression);
                listOfPatterns.add(p);
            }
        }
        return listOfPatterns;
    }

    /**
     * iterates over the specified list of Patterns until
     * it finds one that matches the specified string.  Then
     * returns the Pattern.
     * <p>
     * Order of the supplied list of patterns is important as
     * most common patterns should come first.  Patterns
     * should be strict in general, and all will be
     * used with case sensitivity on.
     * </p>
     * @param string the string to be searched 
     * @param patterns list of patterns
     * @return matching pattern
     */
    protected static final Pattern matchPattern(String string, List<Pattern> patterns)
    {
        Pattern matchedPattern = null;
        for(Pattern p : patterns)
        {
            if(p.matcher(string).matches())
            {
                return p;
            }
        }
        return matchedPattern;
    }

    /**
     * Write a list of string containing a whole line of a document.
     * @param line a list with the words of the given line
     * @param isRtlDominant determines if rtl or ltl is dominant
     * @throws IOException if something went wrong
     */
    private void writeLine(List<WordWithTextPositions> line, boolean isRtlDominant) throws IOException
    {
        int numberOfStrings = line.size();
        for(int i=0; i<numberOfStrings; i++)
        {
            WordWithTextPositions word = line.get(i);
            writeString(word.getText(), word.getTextPositions());
            if (i < numberOfStrings-1)
            {
                writeWordSeparator();
            }
        }
    }

    /**
     * Normalize the given list of TextPositions.
     * @param line list of TextPositions
     * @param isRtlDominant determines if rtl or ltl is dominant 
     * @param hasRtl determines if lines contains rtl formatted text(parts)
     * @return a list of strings, one string for every word
     */
    private List<WordWithTextPositions> normalize(List<TextPosition> line, boolean isRtlDominant, boolean hasRtl)
    {
        LinkedList<WordWithTextPositions> normalized = new LinkedList<WordWithTextPositions>();
        StringBuilder lineBuilder = new StringBuilder();
        List<TextPosition> wordPositions = new ArrayList<TextPosition>();
        // concatenate the pieces of text in opposite order if RTL is dominant
        if (isRtlDominant)
        {
            int numberOfPositions = line.size();
            for(int i = numberOfPositions-1;i>=0;i--)
            {
                lineBuilder = normalizeAdd(normalized, lineBuilder, wordPositions, line.get(i));
            }
        }
        else
        {
            for(TextPosition text : line)
            {
                lineBuilder = normalizeAdd(normalized, lineBuilder, wordPositions, text);
            }
        }
        if (lineBuilder.length() > 0) 
        {
            normalized.add(createWord(lineBuilder.toString(), wordPositions));
        }
        return normalized;
    }

    /**
     * Used within {@link #normalize(List, boolean, boolean)} to create a single {@link WordWithTextPositions}
     * entry.
     */
    private WordWithTextPositions createWord(String word, List<TextPosition> wordPositions)
    {
        return new WordWithTextPositions(normalize.normalizePres(word), wordPositions);
    }

    /**
     * Used within {@link #normalize(List, boolean, boolean)} to handle a {@link TextPosition}.
     * @return The StringBuilder that must be used when calling this method.
     */
    private StringBuilder normalizeAdd(LinkedList<WordWithTextPositions> normalized,
            StringBuilder lineBuilder, List<TextPosition> wordPositions, TextPosition text)
    {
        if (text instanceof WordSeparator) 
        {
            normalized.add(createWord(lineBuilder.toString(), new ArrayList<TextPosition>(wordPositions)));
            lineBuilder = new StringBuilder();
            wordPositions.clear();
        }
        else 
        {
            lineBuilder.append(text.getCharacter());
            wordPositions.add(text);
        }
        return lineBuilder;
    }

    /**
     * internal marker class.  Used as a place holder in
     * a line of TextPositions.
     * @author ME21969
     *
     */
    private static final class WordSeparator extends TextPosition
    {
        private static final WordSeparator separator = new WordSeparator();
        
        private WordSeparator()
        {
        }

        public static final WordSeparator getSeparator()
        {
            return separator;
        }
    }

    /**
     * Internal class that maps strings to lists of {@link TextPosition} arrays.
     * Note that the number of entries in that list may differ from the number of characters in the
     * string due to normalization.
     *
     * @author Axel Doerfler
     */
    private static final class WordWithTextPositions
    {
        protected String text;
        protected List<TextPosition> textPositions;
        
        public WordWithTextPositions(String word, List<TextPosition> positions)
        {
            text = word;
            textPositions = positions;
        }
        
        public String getText()
        {
            return text;
        }

        public List<TextPosition> getTextPositions()
        {
            return textPositions;
        }
    }
}
