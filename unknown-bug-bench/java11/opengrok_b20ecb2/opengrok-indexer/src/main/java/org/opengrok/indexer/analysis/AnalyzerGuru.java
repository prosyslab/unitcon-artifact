/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright (c) 2005, 2023, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017, 2021, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.BytesRef;
import org.jetbrains.annotations.Nullable;
import org.opengrok.indexer.analysis.FileAnalyzerFactory.Matcher;
import org.opengrok.indexer.analysis.ada.AdaAnalyzerFactory;
import org.opengrok.indexer.analysis.archive.BZip2AnalyzerFactory;
import org.opengrok.indexer.analysis.archive.GZIPAnalyzerFactory;
import org.opengrok.indexer.analysis.archive.TarAnalyzerFactory;
import org.opengrok.indexer.analysis.archive.ZipAnalyzerFactory;
import org.opengrok.indexer.analysis.asm.AsmAnalyzerFactory;
import org.opengrok.indexer.analysis.c.CAnalyzerFactory;
import org.opengrok.indexer.analysis.c.CxxAnalyzerFactory;
import org.opengrok.indexer.analysis.clojure.ClojureAnalyzerFactory;
import org.opengrok.indexer.analysis.csharp.CSharpAnalyzerFactory;
import org.opengrok.indexer.analysis.data.IgnorantAnalyzerFactory;
import org.opengrok.indexer.analysis.data.ImageAnalyzerFactory;
import org.opengrok.indexer.analysis.document.MandocAnalyzerFactory;
import org.opengrok.indexer.analysis.document.TroffAnalyzerFactory;
import org.opengrok.indexer.analysis.eiffel.EiffelAnalyzerFactory;
import org.opengrok.indexer.analysis.erlang.ErlangAnalyzerFactory;
import org.opengrok.indexer.analysis.executables.ELFAnalyzerFactory;
import org.opengrok.indexer.analysis.executables.JarAnalyzerFactory;
import org.opengrok.indexer.analysis.executables.JavaClassAnalyzerFactory;
import org.opengrok.indexer.analysis.fortran.FortranAnalyzerFactory;
import org.opengrok.indexer.analysis.golang.GolangAnalyzerFactory;
import org.opengrok.indexer.analysis.haskell.HaskellAnalyzerFactory;
import org.opengrok.indexer.analysis.hcl.HCLAnalyzerFactory;
import org.opengrok.indexer.analysis.java.JavaAnalyzerFactory;
import org.opengrok.indexer.analysis.javascript.JavaScriptAnalyzerFactory;
import org.opengrok.indexer.analysis.json.JsonAnalyzerFactory;
import org.opengrok.indexer.analysis.kotlin.KotlinAnalyzerFactory;
import org.opengrok.indexer.analysis.lisp.LispAnalyzerFactory;
import org.opengrok.indexer.analysis.lua.LuaAnalyzerFactory;
import org.opengrok.indexer.analysis.pascal.PascalAnalyzerFactory;
import org.opengrok.indexer.analysis.perl.PerlAnalyzerFactory;
import org.opengrok.indexer.analysis.php.PhpAnalyzerFactory;
import org.opengrok.indexer.analysis.plain.PlainAnalyzerFactory;
import org.opengrok.indexer.analysis.plain.XMLAnalyzerFactory;
import org.opengrok.indexer.analysis.powershell.PowershellAnalyzerFactory;
import org.opengrok.indexer.analysis.python.PythonAnalyzerFactory;
import org.opengrok.indexer.analysis.r.RAnalyzerFactory;
import org.opengrok.indexer.analysis.ruby.RubyAnalyzerFactory;
import org.opengrok.indexer.analysis.rust.RustAnalyzerFactory;
import org.opengrok.indexer.analysis.scala.ScalaAnalyzerFactory;
import org.opengrok.indexer.analysis.sh.ShAnalyzerFactory;
import org.opengrok.indexer.analysis.sql.PLSQLAnalyzerFactory;
import org.opengrok.indexer.analysis.sql.SQLAnalyzerFactory;
import org.opengrok.indexer.analysis.swift.SwiftAnalyzerFactory;
import org.opengrok.indexer.analysis.tcl.TclAnalyzerFactory;
import org.opengrok.indexer.analysis.terraform.TerraformAnalyzerFactory;
import org.opengrok.indexer.analysis.typescript.TypeScriptAnalyzerFactory;
import org.opengrok.indexer.analysis.uue.UuencodeAnalyzerFactory;
import org.opengrok.indexer.analysis.vb.VBAnalyzerFactory;
import org.opengrok.indexer.analysis.verilog.VerilogAnalyzerFactory;
import org.opengrok.indexer.analysis.yaml.YamlAnalyzerFactory;
import org.opengrok.indexer.configuration.Project;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.history.Annotation;
import org.opengrok.indexer.history.History;
import org.opengrok.indexer.history.HistoryEntry;
import org.opengrok.indexer.history.HistoryException;
import org.opengrok.indexer.history.HistoryGuru;
import org.opengrok.indexer.history.HistoryReader;
import org.opengrok.indexer.logger.LoggerFactory;
import org.opengrok.indexer.search.QueryBuilder;
import org.opengrok.indexer.util.IOUtils;
import org.opengrok.indexer.util.Statistics;
import org.opengrok.indexer.util.WrapperIOException;
import org.opengrok.indexer.web.Util;

/**
 * Manages and provides Analyzers as needed. Please see
 * <a href="https://github.com/oracle/opengrok/wiki/Internals">
 * this</a> page for a great description of the purpose of the AnalyzerGuru.
 * <p>
 * Created on September 22, 2005
 * </p>
 * @author Chandan
 */
public class AnalyzerGuru {

    /**
     * The maximum number of characters (multi-byte if a BOM is identified) to
     * read from the input stream to be used for magic string matching.
     */
    private static final int OPENING_MAX_CHARS = 100;

    /**
     * Set to 16K -- though debugging shows it would do with only 8K+3
     * (standard buffer for Java BufferedInputStream plus 3 bytes for largest UTF BOM).
     */
    private static final int MARK_READ_LIMIT = 1024 * 16;

    /**
     * The number of bytes read from the start of the file for magic number or
     * string analysis. Some {@link FileAnalyzerFactory.Matcher}
     * implementations may read more data subsequently, but this field defines
     * the number of bytes initially read for general matching.
     */
    private static final int MAGIC_BYTES_NUM = 8;

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzerGuru.class);

    /**
     * The default {@code FileAnalyzerFactory} instance.
     */
    private static final AnalyzerFactory DEFAULT_ANALYZER_FACTORY = new FileAnalyzerFactory();

    /**
     * Map from file names to analyzer factories.
     */
    private static final Map<String, AnalyzerFactory> FILE_NAMES = new HashMap<>();

    /**
     * Map from file extensions to analyzer factories.
     */
    private static final Map<String, AnalyzerFactory> ext = new HashMap<>();

    /**
     * Map from file prefixes to analyzer factories.
     */
    private static final Map<String, AnalyzerFactory> pre = new HashMap<>();

    /**
     * Appended when
     * {@link #addExtension(java.lang.String, AnalyzerFactory)}
     * or
     * {@link #addPrefix(java.lang.String, AnalyzerFactory)}
     * are called to augment the value in {@link #getVersionNo()}.
     */
    private static final TreeSet<String> CUSTOMIZATION_KEYS = new TreeSet<>();

    private static int customizationHashCode;

    /**
     * Descending string length comparator for magics.
     */
    private static final Comparator<String> descStrlenComparator = (s1, s2) -> {
        // DESC: s2 length <=> s1 length
        var cmp = Integer.compare(s2.length(), s1.length());
        if (cmp != 0) {
            return cmp;
        }

        // the Comparator must also be "consistent with equals", so check
        // string contents too when (length)cmp == 0. (ASC: s1 <=> s2.)
        cmp = s1.compareTo(s2);
        return cmp;
    };

    /**
     * Map from magic strings to analyzer factories.
     */
    private static final SortedMap<String, AnalyzerFactory> magics =
        new TreeMap<>(descStrlenComparator);

    /**
     * List of matcher objects which can be used to determine which analyzer
     * factory to use.
     */
    private static final List<FileAnalyzerFactory.Matcher> matchers = new ArrayList<>();

    /**
     * List of all registered {@code FileAnalyzerFactory} instances.
     */
    private static final List<AnalyzerFactory> factories = new ArrayList<>();

    /**
     * Names of all analysis packages.
     */
    private static final List<String> analysisPkgNames = new ArrayList<>();

    public static final FieldType string_ft_stored_nanalyzed_norms = new FieldType(StringField.TYPE_STORED);
    public static final FieldType string_ft_nstored_nanalyzed_norms = new FieldType(StringField.TYPE_NOT_STORED);

    private static final Map<String, String> fileTypeDescriptions = new TreeMap<>();

    /**
     * Maps from {@link FileAnalyzer#getFileTypeName()} to
     * {@link FileAnalyzerFactory}.
     */
    private static final Map<String, AnalyzerFactory> FILETYPE_FACTORIES =
            new HashMap<>();

    /**
     * Maps from {@link FileAnalyzer#getFileTypeName()} to
     * {@link FileAnalyzer#getVersionNo()}.
     */
    private static final Map<String, Long> ANALYZER_VERSIONS = new HashMap<>();

    private static final LangTreeMap langMap = new LangTreeMap();
    private static final LangTreeMap defaultLangMap = new LangTreeMap();

    /*
     * If you write your own analyzer please register it here. The order is
     * important for any factory that uses a FileAnalyzerFactory.Matcher
     * implementation, as those are run in the same order as defined below --
     * though precise Matchers are run before imprecise ones.
     */
    static {
        try {
            AnalyzerFactory[] analyzers = {
                DEFAULT_ANALYZER_FACTORY,
                new IgnorantAnalyzerFactory(),
                new BZip2AnalyzerFactory(),
                new XMLAnalyzerFactory(),
                new YamlAnalyzerFactory(),
                MandocAnalyzerFactory.DEFAULT_INSTANCE,
                TroffAnalyzerFactory.DEFAULT_INSTANCE,
                new ELFAnalyzerFactory(),
                JavaClassAnalyzerFactory.DEFAULT_INSTANCE,
                new ImageAnalyzerFactory(),
                JarAnalyzerFactory.DEFAULT_INSTANCE,
                ZipAnalyzerFactory.DEFAULT_INSTANCE,
                new TarAnalyzerFactory(),
                new CAnalyzerFactory(),
                new CSharpAnalyzerFactory(),
                new VBAnalyzerFactory(),
                new CxxAnalyzerFactory(),
                new ErlangAnalyzerFactory(),
                new ShAnalyzerFactory(),
                new PowershellAnalyzerFactory(),
                new UuencodeAnalyzerFactory(),
                new GZIPAnalyzerFactory(),
                new JavaAnalyzerFactory(),
                new JavaScriptAnalyzerFactory(),
                new KotlinAnalyzerFactory(),
                new SwiftAnalyzerFactory(),
                new JsonAnalyzerFactory(),
                new PythonAnalyzerFactory(),
                new RustAnalyzerFactory(),
                new PerlAnalyzerFactory(),
                new PhpAnalyzerFactory(),
                new LispAnalyzerFactory(),
                new TclAnalyzerFactory(),
                new ScalaAnalyzerFactory(),
                new ClojureAnalyzerFactory(),
                new SQLAnalyzerFactory(),
                new PLSQLAnalyzerFactory(),
                new FortranAnalyzerFactory(),
                new HaskellAnalyzerFactory(),
                new GolangAnalyzerFactory(),
                new LuaAnalyzerFactory(),
                new PascalAnalyzerFactory(),
                new AdaAnalyzerFactory(),
                new RubyAnalyzerFactory(),
                new EiffelAnalyzerFactory(),
                new VerilogAnalyzerFactory(),
                new TypeScriptAnalyzerFactory(),
                new AsmAnalyzerFactory(),
                new HCLAnalyzerFactory(),
                new TerraformAnalyzerFactory(),
                new RAnalyzerFactory(),
                // Keep PlainAnalyzer last, with its lone, quite fuzzy matcher.
                PlainAnalyzerFactory.DEFAULT_INSTANCE
            };

            for (AnalyzerFactory analyzer : analyzers) {
                registerAnalyzer(analyzer);
            }

            for (AnalyzerFactory analyzer : analyzers) {
                if (analyzer.getName() != null && !analyzer.getName().isEmpty()) {
                    fileTypeDescriptions.put(analyzer.getAnalyzer().getFileTypeName(), analyzer.getName());
                }
            }

            string_ft_stored_nanalyzed_norms.setOmitNorms(false);
            string_ft_nstored_nanalyzed_norms.setOmitNorms(false);
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE,
                    "exception hit when constructing AnalyzerGuru static", t);
            throw t;
        }
    }

    /**
     * Gets a version number to be used to tag documents examined by the guru so
     * that {@link AbstractAnalyzer} selection can be re-done later if a stored
     * version number is different from the current implementation or if guru
     * factory registrations are modified by the user to change the guru
     * operation.
     * <p>
     * The static part of the version is bumped in a release when e.g. new
     * {@link FileAnalyzerFactory} subclasses are registered or when existing
     * {@link FileAnalyzerFactory} subclasses are revised to target more or
     * different files.
     * @return a value whose lower 32-bits are a static value
     * 20230921_00
     * for the current implementation and whose higher-32 bits are non-zero if
     * {@link #addExtension(java.lang.String, AnalyzerFactory)}
     * or
     * {@link #addPrefix(java.lang.String, AnalyzerFactory)}
     * has been called.
     */
    public static long getVersionNo() {
        final int ver32 = 20230921_00; // Edit comment above too!
        long ver = ver32;
        if (customizationHashCode != 0) {
            ver |= (long) customizationHashCode << 32;
        }
        return ver;
    }

    /**
     * Gets a version number according to a registered
     * {@link FileAnalyzer#getVersionNo()} for a {@code fileTypeName} according
     * to {@link FileAnalyzer#getFileTypeName()}.
     * @param fileTypeName a defined instance
     * @return a registered value or {@link Long#MIN_VALUE} if
     * {@code fileTypeName} is unknown
     */
    public static long getAnalyzerVersionNo(String fileTypeName) {
        return ANALYZER_VERSIONS.getOrDefault(fileTypeName, Long.MIN_VALUE);
    }

    public static Map<String, Long> getAnalyzersVersionNos() {
        return Collections.unmodifiableMap(ANALYZER_VERSIONS);
    }

    public static Map<String, AnalyzerFactory> getExtensionsMap() {
        return Collections.unmodifiableMap(ext);
    }

    public static Map<String, AnalyzerFactory> getPrefixesMap() {
        return Collections.unmodifiableMap(pre);
    }

    public static Map<String, AnalyzerFactory> getMagicsMap() {
        return Collections.unmodifiableMap(magics);
    }

    public static List<Matcher> getAnalyzerFactoryMatchers() {
        return Collections.unmodifiableList(matchers);
    }

    public static Map<String, String> getfileTypeDescriptions() {
        return Collections.unmodifiableMap(fileTypeDescriptions);
    }

    public static List<AnalyzerFactory> getAnalyzerFactories() {
        return Collections.unmodifiableList(factories);
    }

    private static final String USED_IN_MULTIPLE_MSG = "' used in multiple analyzers";

    /**
     * Register a {@code FileAnalyzerFactory} instance.
     */
    private static void registerAnalyzer(AnalyzerFactory factory) {
        for (String name : factory.getFileNames()) {
            AnalyzerFactory old = FILE_NAMES.put(name, factory);
            assert old == null :
                    "name '" + name + USED_IN_MULTIPLE_MSG;
        }
        for (String prefix : factory.getPrefixes()) {
            AnalyzerFactory old = pre.put(prefix, factory);
            assert old == null :
                    "prefix '" + prefix + USED_IN_MULTIPLE_MSG;
        }
        for (String suffix : factory.getSuffixes()) {
            AnalyzerFactory old = ext.put(suffix, factory);
            assert old == null :
                    "suffix '" + suffix + USED_IN_MULTIPLE_MSG;
        }
        for (String magic : factory.getMagicStrings()) {
            AnalyzerFactory old = magics.put(magic, factory);
            assert old == null :
                    "magic '" + magic + USED_IN_MULTIPLE_MSG;
        }
        matchers.addAll(factory.getMatchers());
        factories.add(factory);

        AbstractAnalyzer fa = factory.getAnalyzer();
        String fileTypeName = fa.getFileTypeName();
        FILETYPE_FACTORIES.put(fileTypeName, factory);
        ANALYZER_VERSIONS.put(fileTypeName, fa.getVersionNo());

        // Possibly configure default LANG mappings for the factory.
        String ctagsLang = factory.getAnalyzer().getCtagsLang();
        if (ctagsLang != null) {
            List<String> prefixes = factory.getPrefixes();
            if (prefixes != null) {
                for (String prefix : prefixes) {
                    defaultLangMap.add(prefix, ctagsLang);
                }
            }

            List<String> suffixes = factory.getSuffixes();
            if (suffixes != null) {
                for (String suffix : suffixes) {
                    // LangMap needs a "." to signify a file extension.
                    defaultLangMap.add("." + suffix, ctagsLang);
                }
            }
        }
    }

    /**
     * Instruct the AnalyzerGuru to use a given analyzer for a given file
     * prefix.
     *
     * @param prefix the file prefix to add
     * @param factory a factory which creates the analyzer to use for the given
     * extension (if you pass null as the analyzer, you will disable the
     * analyzer used for that extension)
     */
    public static void addPrefix(String prefix, AnalyzerFactory factory) {
        AnalyzerFactory oldFactory;
        if (factory == null) {
            oldFactory = pre.remove(prefix);
            langMap.exclude(prefix);
        } else {
            oldFactory = pre.put(prefix, factory);
            langMap.add(prefix, factory.getAnalyzer().getCtagsLang());
        }

        if (factoriesDifferent(factory, oldFactory)) {
            addCustomizationKey("p:" + prefix);
        }
    }

    /**
     * Instruct the AnalyzerGuru to use a given analyzer for a given file
     * extension.
     *
     * @param extension the file-extension to add
     * @param factory a factory which creates the analyzer to use for the given
     * extension (if you pass null as the analyzer, you will disable the
     * analyzer used for that extension)
     * @throws IllegalArgumentException if {@code extension} contains a period
     */
    public static void addExtension(String extension, AnalyzerFactory factory) {
        if (extension.contains(".")) {
            throw new IllegalArgumentException("extension contains a '.'");
        }

        // LangMap fileSpec requires a leading period to indicate an extension.
        String langMapExtension = "." + extension;

        AnalyzerFactory oldFactory;
        if (factory == null) {
            oldFactory = ext.remove(extension);
            langMap.exclude(langMapExtension);
        } else {
            oldFactory = ext.put(extension, factory);
            langMap.add(langMapExtension, factory.getAnalyzer().getCtagsLang());
        }

        if (factoriesDifferent(factory, oldFactory)) {
            addCustomizationKey("e:" + extension);
        }
    }

    /**
     * Gets an unmodifiable view of the language mappings resulting from
     * {@link #addExtension(String, AnalyzerFactory)} and
     * {@link #addPrefix(String, AnalyzerFactory)} merged with default language
     * mappings of OpenGrok's analyzers.
     * @return LangMap instance
     */
    public static LangMap getLangMap() {
        return langMap.mergeSecondary(defaultLangMap).unmodifiable();
    }

    /**
     * Get the default Analyzer.
     *
     * @return default FileAnalyzer
     */
    public static AbstractAnalyzer getAnalyzer() {
        return DEFAULT_ANALYZER_FACTORY.getAnalyzer();
    }

    /**
     * Gets an analyzer for the specified {@code fileTypeName} if it accords
     * with a known {@link FileAnalyzer#getFileTypeName()}.
     * @param fileTypeName a defined name
     * @return a defined instance if known or otherwise {@code null}
     */
    public static AbstractAnalyzer getAnalyzer(String fileTypeName) {
        AnalyzerFactory factory = FILETYPE_FACTORIES.get(fileTypeName);
        return factory == null ? null : factory.getAnalyzer();
    }

    /**
     * Get an analyzer suited to analyze a file. This function will reuse
     * analyzers since they are costly.
     *
     * @param in Input stream containing data to be analyzed
     * @param file Name of the file to be analyzed
     * @return An analyzer suited for that file content
     * @throws java.io.IOException If an error occurs while accessing the data
     * in the input stream.
     */
    public static AbstractAnalyzer getAnalyzer(InputStream in, String file) throws IOException {
        AnalyzerFactory factory = find(in, file);
        if (Objects.isNull(factory)) {
            AbstractAnalyzer defaultAnalyzer = getAnalyzer();
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST, "''{0}'': fallback {1}",
                    new Object[]{file, defaultAnalyzer.getClass().getSimpleName()});
            }
            return defaultAnalyzer;
        }
        return factory.getAnalyzer();
    }

    /**
     * Free resources associated with all registered analyzers.
     */
    public static void returnAnalyzers() {
        for (AnalyzerFactory analyzer : factories) {
            analyzer.returnAnalyzer();
        }
    }

    /**
     * Populate a Lucene document with the required fields.
     *
     * @param doc The document to populate
     * @param file The file to index
     * @param path Where the file is located (from source root)
     * @param fa The analyzer to use on the file
     * @param xrefOut Where to write the xref (possibly {@code null})
     * @throws IOException If an exception occurs while collecting the data
     * @throws InterruptedException if a timeout occurs
     */
    public void populateDocument(Document doc, File file, String path, AbstractAnalyzer fa, Writer xrefOut)
            throws IOException, InterruptedException {

        String date = DateTools.timeToString(file.lastModified(),
                DateTools.Resolution.MILLISECOND);
        path = Util.fixPathIfWindows(path);
        doc.add(new Field(QueryBuilder.U, Util.path2uid(path, date),
                string_ft_stored_nanalyzed_norms));
        doc.add(new Field(QueryBuilder.FULLPATH, file.getAbsolutePath(),
                string_ft_nstored_nanalyzed_norms));
        doc.add(new SortedDocValuesField(QueryBuilder.FULLPATH,
                new BytesRef(file.getAbsolutePath())));

        if (RuntimeEnvironment.getInstance().isHistoryEnabled()) {
            populateDocumentHistory(doc, file);
        }
        doc.add(new Field(QueryBuilder.DATE, date, string_ft_stored_nanalyzed_norms));
        doc.add(new SortedDocValuesField(QueryBuilder.DATE, new BytesRef(date)));

        // 'path' is not null, as it was passed to Util.path2uid() above.
        doc.add(new TextField(QueryBuilder.PATH, path, Store.YES));
        Project project = Project.getProject(path);
        if (project != null) {
            doc.add(new TextField(QueryBuilder.PROJECT, project.getPath(), Store.YES));
        }

        /*
         * Use the parent of the path -- not the absolute file as is done for
         * FULLPATH -- so that DIRPATH is the same convention as for PATH
         * above. A StringField, however, is used instead of a TextField.
         */
        File fpath = new File(path);
        String fileParent = fpath.getParent();
        if (fileParent != null && !fileParent.isEmpty()) {
            String normalizedPath = QueryBuilder.normalizeDirPath(fileParent);
            StringField npstring = new StringField(QueryBuilder.DIRPATH, normalizedPath, Store.NO);
            doc.add(npstring);
        }

        if (fa != null) {
            AbstractAnalyzer.Genre g = fa.getGenre();
            if (g == AbstractAnalyzer.Genre.PLAIN || g == AbstractAnalyzer.Genre.XREFABLE || g == AbstractAnalyzer.Genre.HTML) {
                doc.add(new Field(QueryBuilder.T, g.typeName(), string_ft_stored_nanalyzed_norms));
            }
            fa.analyze(doc, StreamSource.fromFile(file), xrefOut);

            String type = fa.getFileTypeName();
            doc.add(new StringField(QueryBuilder.TYPE, type, Store.YES));
        }
    }

    private static void populateDocumentHistory(Document doc, File file) {
        try {
            HistoryGuru histGuru = HistoryGuru.getInstance();
            History history = histGuru.getHistory(file, false);
            if (history != null) {
                HistoryReader hr = new HistoryReader(history);
                doc.add(new TextField(QueryBuilder.HIST, hr));
                HistoryEntry histEntry = history.getLastHistoryEntry();
                if (histEntry != null) {
                    doc.add(new TextField(QueryBuilder.LASTREV, histEntry.getRevision(), Store.YES));
                }
                histGuru.storeHistory(file, history);
            }
        } catch (HistoryException e) {
            LOGGER.log(Level.WARNING, "An error occurred while reading history: ", e);
        }
    }

    /**
     * Write a browse-able version of the file.
     *
     * @param factory The analyzer factory for this file type
     * @param in The input stream containing the data
     * @param out Where to write the result
     * @param defs definitions for the source file, if available
     * @param annotation Annotation information for the file
     * @param project Project the file belongs to
     * @param file file object, used only for logging
     * @throws java.io.IOException If an error occurs while creating the output
     */
    public static void writeXref(AnalyzerFactory factory, Reader in,
            Writer out, @Nullable Definitions defs,
            Annotation annotation, Project project, File file)
            throws IOException {

        Statistics stat = new Statistics();
        Reader input = in;
        if (factory.getGenre() == AbstractAnalyzer.Genre.PLAIN) {
            // This is some kind of text file, so we need to expand tabs to
            // spaces to match the project's tab settings.
            input = ExpandTabsReader.wrap(in, project);
        }

        WriteXrefArgs args = new WriteXrefArgs(input, out);
        args.setDefs(defs);
        args.setAnnotation(annotation);
        args.setProject(project);

        AbstractAnalyzer analyzer = factory.getAnalyzer();
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        analyzer.setScopesEnabled(env.isScopesEnabled());
        analyzer.setFoldingEnabled(env.isFoldingEnabled());
        analyzer.writeXref(args);

        stat.report(LOGGER, Level.FINEST, String.format("wrote xref for '%s'", file), "xref.write.latency");
    }

    /**
     * Writes a browse-able version of the file transformed for immediate serving to a web client.
     * @param contextPath the web context path for
     * {@link Util#dumpXref(java.io.Writer, java.io.Reader, java.lang.String, File)}
     * @param factory the analyzer factory for this file type
     * @param in the input stream containing the data
     * @param out a defined instance to write
     * @param defs definitions for the source file, if available
     * @param annotation annotation information for the file
     * @param project project the file belongs to
     * @param file file object, used for logging only
     * @throws java.io.IOException if an error occurs while creating the output
     */
    @SuppressWarnings({"java:S5443", "java:S107"})
    public static void writeDumpedXref(String contextPath,
            AnalyzerFactory factory, Reader in, Writer out,
            @Nullable Definitions defs, Annotation annotation, Project project, File file) throws IOException {

        File xrefTemp = File.createTempFile("ogxref", ".html");
        try {
            try (FileWriter tmpout = new FileWriter(xrefTemp)) {
                writeXref(factory, in, tmpout, defs, annotation, project, file);
            }
            Util.dumpXref(out, xrefTemp, false, contextPath);
        } finally {
            Files.delete(xrefTemp.toPath());
        }
    }

    /**
     * Get the genre of a file.
     *
     * @param file The file to inspect
     * @return The genre suitable to decide how to display the file or {@code null} if not found
     */
    @Nullable
    public static AbstractAnalyzer.Genre getGenre(String file) {
        return getGenre(find(file));
    }

    /**
     * Get the genre of a bulk of data.
     *
     * @param in A stream containing the data
     * @return The genre suitable to decide how to display the file or {@code null} if not found
     * @throws java.io.IOException If an error occurs while getting the content
     */
    @Nullable
    public static AbstractAnalyzer.Genre getGenre(InputStream in) throws IOException {
        return getGenre(find(in));
    }

    /**
     * Get the genre for a named class (this is most likely an analyzer).
     *
     * @param factory the analyzer factory to get the genre for or {@code null} if not found
     * @return The genre of this class (or {@code null} if not found)
     */
    @Nullable
    public static AbstractAnalyzer.Genre getGenre(AnalyzerFactory factory) {
        return Optional.ofNullable(factory).map(AnalyzerFactory::getGenre).orElse(null);
    }

    /**
     * Finds a {@code FileAnalyzerFactory} for the specified
     * {@link FileAnalyzer#getFileTypeName()}.
     * @param fileTypeName a defined instance
     * @return a defined instance or {@code null}
     */
    public static AnalyzerFactory findByFileTypeName(String fileTypeName) {
        return FILETYPE_FACTORIES.get(fileTypeName);
    }

    /**
     * Find a {@code FileAnalyzerFactory} with the specified class name. If one
     * doesn't exist, create one and register it. Allow specification of either
     * the complete class name (which includes the package name) or the simple
     * name of the class.
     *
     * @param factoryClassName name of the factory class
     * @return a file analyzer factory
     *
     * @throws ClassNotFoundException if there is no class with that name
     * @throws ClassCastException if the class is not a subclass of {@code
     * FileAnalyzerFactory}
     * @throws IllegalAccessException if the constructor cannot be accessed
     * @throws InstantiationException if the class cannot be instantiated
     * @throws NoSuchMethodException if no-argument constructor could not be found
     * @throws InvocationTargetException if the underlying constructor throws an exception
     */
    public static AnalyzerFactory findFactory(String factoryClassName)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException,
            InvocationTargetException {
        Class<?> fcn;
        try {
            fcn = Class.forName(factoryClassName);

        } catch (ClassNotFoundException e) {
            fcn = getFactoryClass(factoryClassName);

            if (fcn == null) {
                throw new ClassNotFoundException("Unable to locate class " + factoryClassName);
            }
        }

        return findFactory(fcn);
    }

    /**
     * Get Analyzer factory class using class simple name.
     *
     * @param simpleName which may be either the factory class
     * simple name (eg. CAnalyzerFactory), the analyzer name
     * (eg. CAnalyzer), or the language name (eg. C)
     *
     * @return the analyzer factory class, or null when not found.
     */
    public static Class<?> getFactoryClass(String simpleName) {
        Class<?> factoryClass = null;

        // Build analysis package name list first time only
        if (analysisPkgNames.isEmpty()) {
            Package[] p = Package.getPackages();
            for (Package pp : p) {
                String pname = pp.getName();
                if (pname.contains(".analysis.")) {
                    analysisPkgNames.add(pname);
                }
            }
        }

        // This allows user to enter the language or analyzer name
        // (eg. C or CAnalyzer vs. CAnalyzerFactory)
        // Note that this assumes a regular naming scheme of
        // all language parsers:
        //      <language>Analyzer, <language>AnalyzerFactory

        if (!simpleName.contains("Analyzer")) {
            simpleName += "Analyzer";
        }

        if (!simpleName.contains("Factory")) {
            simpleName += "Factory";
        }

        for (String aPackage : analysisPkgNames) {
            try {
                String fqn = aPackage + "." + simpleName;
                factoryClass = Class.forName(fqn);
                break;
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }

        return factoryClass;
    }

    /**
     * Find a {@code FileAnalyzerFactory} which is an instance of the specified
     * class. If one doesn't exist, create one and register it.
     *
     * @param factoryClass the factory class
     * @return a file analyzer factory
     *
     * @throws ClassCastException if the class is not a subclass of {@code FileAnalyzerFactory}
     * @throws IllegalAccessException if the constructor cannot be accessed
     * @throws InstantiationException if the class cannot be instantiated
     * @throws NoSuchMethodException if no-argument constructor could not be found
     * @throws InvocationTargetException if the underlying constructor throws an exception
     */
    private static AnalyzerFactory findFactory(Class<?> factoryClass)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        for (AnalyzerFactory f : factories) {
            if (f.getClass() == factoryClass) {
                return f;
            }
        }
        AnalyzerFactory f = (AnalyzerFactory) factoryClass.getDeclaredConstructor().newInstance();
        registerAnalyzer(f);
        return f;
    }

    /**
     * Finds a suitable analyzer class for file name. If the analyzer cannot be
     * determined by the file extension, try to look at the data in the
     * {@link InputStream} to find a suitable analyzer.
     * <p>
     * Use if you just want to find file type.
     * </p>
     * @param in The input stream containing the data
     * @param file The file name to get the analyzer for
     * @return the analyzer factory to use
     * @throws java.io.IOException If a problem occurred while reading the data
     */
    public static AnalyzerFactory find(InputStream in, String file) throws IOException {
        AnalyzerFactory factory = find(file);

        if (factory != null) {
            return factory;
        }
        return findForStream(in, file);
    }

    /**
     * Finds a suitable analyser class for file name.
     *
     * @param file The file name to get the analyzer for
     * @return the analyzer factory to use
     */
    public static AnalyzerFactory find(String file) {
        String path;
        var i = file.lastIndexOf(File.separatorChar);

        // Get basename of the file first.
        if (i  > 0 && (i + 1 < file.length())) {
            path = file.substring(i + 1);
        } else {
            path = file;
        }

        var dotPos = path.lastIndexOf('.');
        Optional<AnalyzerFactory> optionalFactory = Optional.empty();
        if (dotPos >= 0) {
            // Try matching the prefix and then by suffix.
            // We kind of consider this order (first
            // prefix then suffix) to be workable although for sure there can be
            // cases when this does not work.
            optionalFactory = findByPrefix(file, path, dotPos)
                    .or(() -> findBySuffix(file, path, dotPos));

        }

        // file doesn't have any of the prefix or extensions we know, try full match
        return optionalFactory
                .orElseGet(() -> FILE_NAMES.get(path.toUpperCase(Locale.ROOT)));
    }

    private static Optional<AnalyzerFactory> findBySuffix(String file, String path, int dotPos) {
        var suffixFactory = Optional.of(dotPos)
                .map(pos -> path.substring(dotPos + 1))
                .map(suffix -> suffix.toUpperCase(Locale.ROOT))
                .map(ext::get);
        suffixFactory.ifPresent(factory -> {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST, "''{0}'': chosen by suffix: {1}",
                        new Object[]{file, factory.getClass().getSimpleName()});
            }
        });
        return suffixFactory;
    }

    private static Optional<AnalyzerFactory> findByPrefix(String file, String path, int dotPos) {
        var prefixFactory = Optional.of(dotPos)
                .map(pos -> path.substring(0, dotPos))
                .filter(prefix -> !prefix.isEmpty())
                .map(prefix -> prefix.toUpperCase(Locale.ROOT))
                .map(pre::get);
        prefixFactory.ifPresent(factory -> {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST, "''{0}'': chosen by prefix: {1}",
                        new Object[]{file, factory.getClass().getSimpleName()});
            }
        });
        return prefixFactory;
    }

    /**
     * Finds a suitable analyzer class for the data in this stream.
     *
     * @param in The stream containing the data to analyze
     * @return the analyzer factory to use
     * @throws java.io.IOException if an error occurs while reading data from
     * the stream
     */
    public static AnalyzerFactory find(InputStream in) throws IOException {
        return findForStream(in, "<anonymous>");
    }

    /**
     * Finds a suitable analyzer class for the data in this stream
     * corresponding to a file of the specified name.
     *
     * @param in The stream containing the data to analyze
     * @param file The file name to get the analyzer for
     * @return the analyzer factory to use
     * @throws java.io.IOException if an error occurs while reading data from the stream
     */
    private static AnalyzerFactory findForStream(InputStream in,
        String file) throws IOException {

        in.mark(MAGIC_BYTES_NUM);
        byte[] content = new byte[MAGIC_BYTES_NUM];
        int len = in.read(content);
        in.reset();

        /*
         * Need at least 4 bytes to perform magic string matching.
         */
        if (len < 4) {
            return null;
        }
        var finalContent = Arrays.copyOf(content, len);
        try {
            return findFactoryUsingMatcher(finalContent, in, file, true)
                    .or(() -> findUsingMagicString(finalContent, in, file))
                    .or(() -> findFactoryUsingMatcher(finalContent, in, file, false))
                    .orElse(null);
        } catch (WrapperIOException ex) {
            throw ex.getParentIOException();
        }
    }

    private static Optional<AnalyzerFactory> findFactoryUsingMatcher(byte[] content, InputStream in,
                                                                 String file,
                                                              boolean isPrecise) {
        var optionalFactory = matchers.stream()
                .filter(matcher -> isPrecise == matcher.isPreciseMagic())
                .map(matcher -> {
                    try {
                        return matcher.isMagic(content, in);
                    } catch (IOException e) {
                        throw new WrapperIOException(e);
                    }
                })
                .filter(Objects::nonNull)
                .findFirst();

        optionalFactory.ifPresent(factory -> {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST,
                        "''{0}'': chosen by {1} magic: {2}",
                        new Object[]{file, isPrecise ? "precise" : "imprecise",
                                factory.getClass().getSimpleName()});
            }
        });
        return optionalFactory;

    }

    private static Optional<AnalyzerFactory> findUsingMagicString(byte[] content, InputStream in, String file) {
        String opening;
        try {
            opening = readOpening(in, content);
        } catch (IOException e) {
            throw new WrapperIOException(e);
        }

        // first, try to look up two words in magics
        String fragment = getWords(opening, 2);
        AnalyzerFactory fac = magics.get(fragment);

        // second, try to look up one word in magics
        if (Objects.isNull(fac)) {
            fragment = getWords(opening, 1);
            fac = magics.get(fragment);
        }
        if (fac != null) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST, "''{0}'': chosen by magic {2}: {1}",
                    new Object[]{file, fac.getClass().getSimpleName(), fragment});
            }
            return Optional.of(fac);
        }
        // try to match initial substrings (DESC strlen)
        return magics.entrySet()
                .stream()
                .filter(entry -> opening.startsWith(entry.getKey()))
                .map(entry -> {
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.log(Level.FINEST,
                                "''{0}'': chosen by magic(substr) {2}: {1}",
                                new Object[]{file,
                                        entry.getValue().getClass().getSimpleName(), entry.getKey()}
                        );
                    }
                    return entry.getValue();
                })
                .findFirst();

    }

    /**
     * Extract initial words from a String, or take the entire
     * <code>value</code> if not enough words can be identified. (If
     * <code>n</code> is not 1 or more, returns an empty String.) (A "word"
     * ends at each and every space character.)
     *
     * @param value The source from which words are cut
     * @param n The number of words to try to extract
     * @return The extracted words or <code>""</code>
     */
    private static String getWords(String value, int n) {
        if (n < 1) {
            return "";
        }
        int l = 0;
        while (n-- > 0) {
            int o = l > 0 ? l + 1 : l;
            int i = value.indexOf(' ', o);
            if (i == -1) {
                return value;
            }
            l = i;
        }
        return value.substring(0, l);
    }

    /**
     * Extract an opening string from the input stream, past any BOM, and past
     * any initial whitespace, but only up to <code>OPENING_MAX_CHARS</code> or
     * to the first <code>\n</code> after any non-whitespace. (Hashbang, #!,
     * openings will have superfluous space removed.)
     *
     * @param in The input stream containing the data
     * @param sig The initial sequence of bytes in the input stream
     * @return The extracted string or <code>""</code>
     * @throws java.io.IOException in case of any read error
     */
    private static String readOpening(InputStream in, byte[] sig)
        throws IOException {

        in.mark(MARK_READ_LIMIT);

        var startingString = findAndSkipBomEncoding(in, sig)
                     .map(encoding -> readOpeningFromBomSkippedStream(in, encoding))
                     .orElse("");
        in.reset();
        return startingString;
    }

    private static String readOpeningFromBomSkippedStream(InputStream in, String encoding) {
        var opening = new StringBuilder();
        try {
            var readr = new BufferedReader(new InputStreamReader(in, encoding), OPENING_MAX_CHARS);
            var ignoreWhiteSpace = true;
            var breakOnNewLine = false;
            for (int nRead = 0, r = readr.read(); r != -1 && nRead <= OPENING_MAX_CHARS;
                 r = readr.read(), nRead++) {

                var c = (char) r;
                if (breakOnNewLine && c == '\n') {
                    break;
                }
                if (Character.isWhitespace(c)) {
                    if (!ignoreWhiteSpace) {
                        opening.append(' ');
                        ignoreWhiteSpace = true;
                    }
                } else {
                    opening.append(c);
                    // If the opening starts with "#!", then track so that any
                    // trailing whitespace after the hashbang is ignored.
                    ignoreWhiteSpace = opening.length() == 2 && opening.charAt(0) == '#' && opening.charAt(1) == '!';
                    breakOnNewLine = true;

                }
            }
        } catch (IOException e) {
            throw new WrapperIOException(e);
        }

        return opening.toString();
    }

    /**
     * Identify the BOM encoding or default to utf 8, skips the encoding bytes .
     *  Empty optional is returned if all bom bytes are not skipped
     * @param in The input stream containing the data
     * @param sig The initial sequence of bytes in the input stream
     * @return optional of encoding
     * @throws IOException in case of any read error
     */
    private static Optional<String> findAndSkipBomEncoding(InputStream in, byte[] sig) throws IOException {
        var encoding = IOUtils.findBOMEncoding(sig);
        if (Objects.isNull(encoding)) {
            // SRCROOT is read with UTF-8 as a default.
            encoding = StandardCharsets.UTF_8.name();
        }
        var skipForBOM = IOUtils.skipForBOM(sig);
        if (skipForBOM != 0 && in.skip(skipForBOM) < skipForBOM) {
            return Optional.empty();
        }
        return Optional.of(encoding);
    }

    private static void addCustomizationKey(String k) {
        CUSTOMIZATION_KEYS.add(k);
        Object[] keys = CUSTOMIZATION_KEYS.toArray();
        customizationHashCode = Objects.hash(keys);
    }

    private static boolean factoriesDifferent(AnalyzerFactory a, AnalyzerFactory b) {
        String aName = null;
        if (a != null) {
            aName = a.getName();
            if (aName == null) {
                aName = a.getClass().getSimpleName();
            }
        }
        String bName = null;
        if (b != null) {
            bName = b.getName();
            if (bName == null) {
                bName = b.getClass().getSimpleName();
            }
        }
        if (aName == null && bName == null) {
            return false;
        }
        return aName == null || !aName.equals(bName);
    }


}
