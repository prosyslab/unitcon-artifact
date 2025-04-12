// Generated from com/puppycrawl/tools/checkstyle/grammars/javadoc/JavadocParser.g4 by ANTLR 4.5.1
package com.puppycrawl.tools.checkstyle.grammars.javadoc;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link JavadocParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface JavadocParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link JavadocParser#javadoc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJavadoc(JavadocParser.JavadocContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#htmlElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlElement(JavadocParser.HtmlElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#htmlElementOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlElementOpen(JavadocParser.HtmlElementOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#htmlElementClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlElementClose(JavadocParser.HtmlElementCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribute(JavadocParser.AttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#htmlTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlTag(JavadocParser.HtmlTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#pTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPTagOpen(JavadocParser.PTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#pTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPTagClose(JavadocParser.PTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#paragraph}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParagraph(JavadocParser.ParagraphContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#liTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiTagOpen(JavadocParser.LiTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#liTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiTagClose(JavadocParser.LiTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#li}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLi(JavadocParser.LiContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#trTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrTagOpen(JavadocParser.TrTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#trTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrTagClose(JavadocParser.TrTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#tr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTr(JavadocParser.TrContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#tdTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTdTagOpen(JavadocParser.TdTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#tdTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTdTagClose(JavadocParser.TdTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#td}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTd(JavadocParser.TdContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#thTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThTagOpen(JavadocParser.ThTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#thTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThTagClose(JavadocParser.ThTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#th}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTh(JavadocParser.ThContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#bodyTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBodyTagOpen(JavadocParser.BodyTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#bodyTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBodyTagClose(JavadocParser.BodyTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBody(JavadocParser.BodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#colgroupTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColgroupTagOpen(JavadocParser.ColgroupTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#colgroupTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColgroupTagClose(JavadocParser.ColgroupTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#colgroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColgroup(JavadocParser.ColgroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#ddTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDdTagOpen(JavadocParser.DdTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#ddTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDdTagClose(JavadocParser.DdTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#dd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDd(JavadocParser.DdContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#dtTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDtTagOpen(JavadocParser.DtTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#dtTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDtTagClose(JavadocParser.DtTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#dt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDt(JavadocParser.DtContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#headTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeadTagOpen(JavadocParser.HeadTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#headTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeadTagClose(JavadocParser.HeadTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#head}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHead(JavadocParser.HeadContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#htmlTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlTagOpen(JavadocParser.HtmlTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#htmlTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlTagClose(JavadocParser.HtmlTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#html}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtml(JavadocParser.HtmlContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#optionTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptionTagOpen(JavadocParser.OptionTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#optionTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptionTagClose(JavadocParser.OptionTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#option}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOption(JavadocParser.OptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#tbodyTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTbodyTagOpen(JavadocParser.TbodyTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#tbodyTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTbodyTagClose(JavadocParser.TbodyTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#tbody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTbody(JavadocParser.TbodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#tfootTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTfootTagOpen(JavadocParser.TfootTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#tfootTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTfootTagClose(JavadocParser.TfootTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#tfoot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTfoot(JavadocParser.TfootContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#theadTagOpen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTheadTagOpen(JavadocParser.TheadTagOpenContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#theadTagClose}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTheadTagClose(JavadocParser.TheadTagCloseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#thead}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThead(JavadocParser.TheadContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#singletonElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingletonElement(JavadocParser.SingletonElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#singletonTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingletonTag(JavadocParser.SingletonTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#areaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAreaTag(JavadocParser.AreaTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#baseTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBaseTag(JavadocParser.BaseTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#basefrontTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBasefrontTag(JavadocParser.BasefrontTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#brTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBrTag(JavadocParser.BrTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#colTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColTag(JavadocParser.ColTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#frameTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFrameTag(JavadocParser.FrameTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#hrTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHrTag(JavadocParser.HrTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#imgTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImgTag(JavadocParser.ImgTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#inputTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputTag(JavadocParser.InputTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#isindexTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsindexTag(JavadocParser.IsindexTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#linkTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLinkTag(JavadocParser.LinkTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#metaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMetaTag(JavadocParser.MetaTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#paramTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamTag(JavadocParser.ParamTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#wrongSinletonTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWrongSinletonTag(JavadocParser.WrongSinletonTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#singletonTagName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingletonTagName(JavadocParser.SingletonTagNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#description}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDescription(JavadocParser.DescriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#reference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReference(JavadocParser.ReferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameters(JavadocParser.ParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#javadocTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJavadocTag(JavadocParser.JavadocTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#javadocInlineTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJavadocInlineTag(JavadocParser.JavadocInlineTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#htmlComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlComment(JavadocParser.HtmlCommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavadocParser#text}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitText(JavadocParser.TextContext ctx);
}