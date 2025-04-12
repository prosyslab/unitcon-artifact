// Generated from com/puppycrawl/tools/checkstyle/grammars/javadoc/JavadocParser.g4 by ANTLR 4.5.1
package com.puppycrawl.tools.checkstyle.grammars.javadoc;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JavadocParser}.
 */
public interface JavadocParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JavadocParser#javadoc}.
	 * @param ctx the parse tree
	 */
	void enterJavadoc(JavadocParser.JavadocContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#javadoc}.
	 * @param ctx the parse tree
	 */
	void exitJavadoc(JavadocParser.JavadocContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void enterHtmlElement(JavadocParser.HtmlElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void exitHtmlElement(JavadocParser.HtmlElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#htmlElementOpen}.
	 * @param ctx the parse tree
	 */
	void enterHtmlElementOpen(JavadocParser.HtmlElementOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#htmlElementOpen}.
	 * @param ctx the parse tree
	 */
	void exitHtmlElementOpen(JavadocParser.HtmlElementOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#htmlElementClose}.
	 * @param ctx the parse tree
	 */
	void enterHtmlElementClose(JavadocParser.HtmlElementCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#htmlElementClose}.
	 * @param ctx the parse tree
	 */
	void exitHtmlElementClose(JavadocParser.HtmlElementCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(JavadocParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(JavadocParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#htmlTag}.
	 * @param ctx the parse tree
	 */
	void enterHtmlTag(JavadocParser.HtmlTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#htmlTag}.
	 * @param ctx the parse tree
	 */
	void exitHtmlTag(JavadocParser.HtmlTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#pTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterPTagOpen(JavadocParser.PTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#pTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitPTagOpen(JavadocParser.PTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#pTagClose}.
	 * @param ctx the parse tree
	 */
	void enterPTagClose(JavadocParser.PTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#pTagClose}.
	 * @param ctx the parse tree
	 */
	void exitPTagClose(JavadocParser.PTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#paragraph}.
	 * @param ctx the parse tree
	 */
	void enterParagraph(JavadocParser.ParagraphContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#paragraph}.
	 * @param ctx the parse tree
	 */
	void exitParagraph(JavadocParser.ParagraphContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#liTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterLiTagOpen(JavadocParser.LiTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#liTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitLiTagOpen(JavadocParser.LiTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#liTagClose}.
	 * @param ctx the parse tree
	 */
	void enterLiTagClose(JavadocParser.LiTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#liTagClose}.
	 * @param ctx the parse tree
	 */
	void exitLiTagClose(JavadocParser.LiTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#li}.
	 * @param ctx the parse tree
	 */
	void enterLi(JavadocParser.LiContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#li}.
	 * @param ctx the parse tree
	 */
	void exitLi(JavadocParser.LiContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#trTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterTrTagOpen(JavadocParser.TrTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#trTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitTrTagOpen(JavadocParser.TrTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#trTagClose}.
	 * @param ctx the parse tree
	 */
	void enterTrTagClose(JavadocParser.TrTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#trTagClose}.
	 * @param ctx the parse tree
	 */
	void exitTrTagClose(JavadocParser.TrTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#tr}.
	 * @param ctx the parse tree
	 */
	void enterTr(JavadocParser.TrContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#tr}.
	 * @param ctx the parse tree
	 */
	void exitTr(JavadocParser.TrContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#tdTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterTdTagOpen(JavadocParser.TdTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#tdTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitTdTagOpen(JavadocParser.TdTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#tdTagClose}.
	 * @param ctx the parse tree
	 */
	void enterTdTagClose(JavadocParser.TdTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#tdTagClose}.
	 * @param ctx the parse tree
	 */
	void exitTdTagClose(JavadocParser.TdTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#td}.
	 * @param ctx the parse tree
	 */
	void enterTd(JavadocParser.TdContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#td}.
	 * @param ctx the parse tree
	 */
	void exitTd(JavadocParser.TdContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#thTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterThTagOpen(JavadocParser.ThTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#thTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitThTagOpen(JavadocParser.ThTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#thTagClose}.
	 * @param ctx the parse tree
	 */
	void enterThTagClose(JavadocParser.ThTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#thTagClose}.
	 * @param ctx the parse tree
	 */
	void exitThTagClose(JavadocParser.ThTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#th}.
	 * @param ctx the parse tree
	 */
	void enterTh(JavadocParser.ThContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#th}.
	 * @param ctx the parse tree
	 */
	void exitTh(JavadocParser.ThContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#bodyTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterBodyTagOpen(JavadocParser.BodyTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#bodyTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitBodyTagOpen(JavadocParser.BodyTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#bodyTagClose}.
	 * @param ctx the parse tree
	 */
	void enterBodyTagClose(JavadocParser.BodyTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#bodyTagClose}.
	 * @param ctx the parse tree
	 */
	void exitBodyTagClose(JavadocParser.BodyTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#body}.
	 * @param ctx the parse tree
	 */
	void enterBody(JavadocParser.BodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#body}.
	 * @param ctx the parse tree
	 */
	void exitBody(JavadocParser.BodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#colgroupTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterColgroupTagOpen(JavadocParser.ColgroupTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#colgroupTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitColgroupTagOpen(JavadocParser.ColgroupTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#colgroupTagClose}.
	 * @param ctx the parse tree
	 */
	void enterColgroupTagClose(JavadocParser.ColgroupTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#colgroupTagClose}.
	 * @param ctx the parse tree
	 */
	void exitColgroupTagClose(JavadocParser.ColgroupTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#colgroup}.
	 * @param ctx the parse tree
	 */
	void enterColgroup(JavadocParser.ColgroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#colgroup}.
	 * @param ctx the parse tree
	 */
	void exitColgroup(JavadocParser.ColgroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#ddTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterDdTagOpen(JavadocParser.DdTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#ddTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitDdTagOpen(JavadocParser.DdTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#ddTagClose}.
	 * @param ctx the parse tree
	 */
	void enterDdTagClose(JavadocParser.DdTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#ddTagClose}.
	 * @param ctx the parse tree
	 */
	void exitDdTagClose(JavadocParser.DdTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#dd}.
	 * @param ctx the parse tree
	 */
	void enterDd(JavadocParser.DdContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#dd}.
	 * @param ctx the parse tree
	 */
	void exitDd(JavadocParser.DdContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#dtTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterDtTagOpen(JavadocParser.DtTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#dtTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitDtTagOpen(JavadocParser.DtTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#dtTagClose}.
	 * @param ctx the parse tree
	 */
	void enterDtTagClose(JavadocParser.DtTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#dtTagClose}.
	 * @param ctx the parse tree
	 */
	void exitDtTagClose(JavadocParser.DtTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#dt}.
	 * @param ctx the parse tree
	 */
	void enterDt(JavadocParser.DtContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#dt}.
	 * @param ctx the parse tree
	 */
	void exitDt(JavadocParser.DtContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#headTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterHeadTagOpen(JavadocParser.HeadTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#headTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitHeadTagOpen(JavadocParser.HeadTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#headTagClose}.
	 * @param ctx the parse tree
	 */
	void enterHeadTagClose(JavadocParser.HeadTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#headTagClose}.
	 * @param ctx the parse tree
	 */
	void exitHeadTagClose(JavadocParser.HeadTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#head}.
	 * @param ctx the parse tree
	 */
	void enterHead(JavadocParser.HeadContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#head}.
	 * @param ctx the parse tree
	 */
	void exitHead(JavadocParser.HeadContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#htmlTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterHtmlTagOpen(JavadocParser.HtmlTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#htmlTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitHtmlTagOpen(JavadocParser.HtmlTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#htmlTagClose}.
	 * @param ctx the parse tree
	 */
	void enterHtmlTagClose(JavadocParser.HtmlTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#htmlTagClose}.
	 * @param ctx the parse tree
	 */
	void exitHtmlTagClose(JavadocParser.HtmlTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#html}.
	 * @param ctx the parse tree
	 */
	void enterHtml(JavadocParser.HtmlContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#html}.
	 * @param ctx the parse tree
	 */
	void exitHtml(JavadocParser.HtmlContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#optionTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterOptionTagOpen(JavadocParser.OptionTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#optionTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitOptionTagOpen(JavadocParser.OptionTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#optionTagClose}.
	 * @param ctx the parse tree
	 */
	void enterOptionTagClose(JavadocParser.OptionTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#optionTagClose}.
	 * @param ctx the parse tree
	 */
	void exitOptionTagClose(JavadocParser.OptionTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#option}.
	 * @param ctx the parse tree
	 */
	void enterOption(JavadocParser.OptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#option}.
	 * @param ctx the parse tree
	 */
	void exitOption(JavadocParser.OptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#tbodyTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterTbodyTagOpen(JavadocParser.TbodyTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#tbodyTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitTbodyTagOpen(JavadocParser.TbodyTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#tbodyTagClose}.
	 * @param ctx the parse tree
	 */
	void enterTbodyTagClose(JavadocParser.TbodyTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#tbodyTagClose}.
	 * @param ctx the parse tree
	 */
	void exitTbodyTagClose(JavadocParser.TbodyTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#tbody}.
	 * @param ctx the parse tree
	 */
	void enterTbody(JavadocParser.TbodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#tbody}.
	 * @param ctx the parse tree
	 */
	void exitTbody(JavadocParser.TbodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#tfootTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterTfootTagOpen(JavadocParser.TfootTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#tfootTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitTfootTagOpen(JavadocParser.TfootTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#tfootTagClose}.
	 * @param ctx the parse tree
	 */
	void enterTfootTagClose(JavadocParser.TfootTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#tfootTagClose}.
	 * @param ctx the parse tree
	 */
	void exitTfootTagClose(JavadocParser.TfootTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#tfoot}.
	 * @param ctx the parse tree
	 */
	void enterTfoot(JavadocParser.TfootContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#tfoot}.
	 * @param ctx the parse tree
	 */
	void exitTfoot(JavadocParser.TfootContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#theadTagOpen}.
	 * @param ctx the parse tree
	 */
	void enterTheadTagOpen(JavadocParser.TheadTagOpenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#theadTagOpen}.
	 * @param ctx the parse tree
	 */
	void exitTheadTagOpen(JavadocParser.TheadTagOpenContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#theadTagClose}.
	 * @param ctx the parse tree
	 */
	void enterTheadTagClose(JavadocParser.TheadTagCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#theadTagClose}.
	 * @param ctx the parse tree
	 */
	void exitTheadTagClose(JavadocParser.TheadTagCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#thead}.
	 * @param ctx the parse tree
	 */
	void enterThead(JavadocParser.TheadContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#thead}.
	 * @param ctx the parse tree
	 */
	void exitThead(JavadocParser.TheadContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#singletonElement}.
	 * @param ctx the parse tree
	 */
	void enterSingletonElement(JavadocParser.SingletonElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#singletonElement}.
	 * @param ctx the parse tree
	 */
	void exitSingletonElement(JavadocParser.SingletonElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#singletonTag}.
	 * @param ctx the parse tree
	 */
	void enterSingletonTag(JavadocParser.SingletonTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#singletonTag}.
	 * @param ctx the parse tree
	 */
	void exitSingletonTag(JavadocParser.SingletonTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#areaTag}.
	 * @param ctx the parse tree
	 */
	void enterAreaTag(JavadocParser.AreaTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#areaTag}.
	 * @param ctx the parse tree
	 */
	void exitAreaTag(JavadocParser.AreaTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#baseTag}.
	 * @param ctx the parse tree
	 */
	void enterBaseTag(JavadocParser.BaseTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#baseTag}.
	 * @param ctx the parse tree
	 */
	void exitBaseTag(JavadocParser.BaseTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#basefrontTag}.
	 * @param ctx the parse tree
	 */
	void enterBasefrontTag(JavadocParser.BasefrontTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#basefrontTag}.
	 * @param ctx the parse tree
	 */
	void exitBasefrontTag(JavadocParser.BasefrontTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#brTag}.
	 * @param ctx the parse tree
	 */
	void enterBrTag(JavadocParser.BrTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#brTag}.
	 * @param ctx the parse tree
	 */
	void exitBrTag(JavadocParser.BrTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#colTag}.
	 * @param ctx the parse tree
	 */
	void enterColTag(JavadocParser.ColTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#colTag}.
	 * @param ctx the parse tree
	 */
	void exitColTag(JavadocParser.ColTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#frameTag}.
	 * @param ctx the parse tree
	 */
	void enterFrameTag(JavadocParser.FrameTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#frameTag}.
	 * @param ctx the parse tree
	 */
	void exitFrameTag(JavadocParser.FrameTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#hrTag}.
	 * @param ctx the parse tree
	 */
	void enterHrTag(JavadocParser.HrTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#hrTag}.
	 * @param ctx the parse tree
	 */
	void exitHrTag(JavadocParser.HrTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#imgTag}.
	 * @param ctx the parse tree
	 */
	void enterImgTag(JavadocParser.ImgTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#imgTag}.
	 * @param ctx the parse tree
	 */
	void exitImgTag(JavadocParser.ImgTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#inputTag}.
	 * @param ctx the parse tree
	 */
	void enterInputTag(JavadocParser.InputTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#inputTag}.
	 * @param ctx the parse tree
	 */
	void exitInputTag(JavadocParser.InputTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#isindexTag}.
	 * @param ctx the parse tree
	 */
	void enterIsindexTag(JavadocParser.IsindexTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#isindexTag}.
	 * @param ctx the parse tree
	 */
	void exitIsindexTag(JavadocParser.IsindexTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#linkTag}.
	 * @param ctx the parse tree
	 */
	void enterLinkTag(JavadocParser.LinkTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#linkTag}.
	 * @param ctx the parse tree
	 */
	void exitLinkTag(JavadocParser.LinkTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#metaTag}.
	 * @param ctx the parse tree
	 */
	void enterMetaTag(JavadocParser.MetaTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#metaTag}.
	 * @param ctx the parse tree
	 */
	void exitMetaTag(JavadocParser.MetaTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#paramTag}.
	 * @param ctx the parse tree
	 */
	void enterParamTag(JavadocParser.ParamTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#paramTag}.
	 * @param ctx the parse tree
	 */
	void exitParamTag(JavadocParser.ParamTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#wrongSinletonTag}.
	 * @param ctx the parse tree
	 */
	void enterWrongSinletonTag(JavadocParser.WrongSinletonTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#wrongSinletonTag}.
	 * @param ctx the parse tree
	 */
	void exitWrongSinletonTag(JavadocParser.WrongSinletonTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#singletonTagName}.
	 * @param ctx the parse tree
	 */
	void enterSingletonTagName(JavadocParser.SingletonTagNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#singletonTagName}.
	 * @param ctx the parse tree
	 */
	void exitSingletonTagName(JavadocParser.SingletonTagNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#description}.
	 * @param ctx the parse tree
	 */
	void enterDescription(JavadocParser.DescriptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#description}.
	 * @param ctx the parse tree
	 */
	void exitDescription(JavadocParser.DescriptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#reference}.
	 * @param ctx the parse tree
	 */
	void enterReference(JavadocParser.ReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#reference}.
	 * @param ctx the parse tree
	 */
	void exitReference(JavadocParser.ReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(JavadocParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(JavadocParser.ParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#javadocTag}.
	 * @param ctx the parse tree
	 */
	void enterJavadocTag(JavadocParser.JavadocTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#javadocTag}.
	 * @param ctx the parse tree
	 */
	void exitJavadocTag(JavadocParser.JavadocTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#javadocInlineTag}.
	 * @param ctx the parse tree
	 */
	void enterJavadocInlineTag(JavadocParser.JavadocInlineTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#javadocInlineTag}.
	 * @param ctx the parse tree
	 */
	void exitJavadocInlineTag(JavadocParser.JavadocInlineTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#htmlComment}.
	 * @param ctx the parse tree
	 */
	void enterHtmlComment(JavadocParser.HtmlCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#htmlComment}.
	 * @param ctx the parse tree
	 */
	void exitHtmlComment(JavadocParser.HtmlCommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavadocParser#text}.
	 * @param ctx the parse tree
	 */
	void enterText(JavadocParser.TextContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavadocParser#text}.
	 * @param ctx the parse tree
	 */
	void exitText(JavadocParser.TextContext ctx);
}