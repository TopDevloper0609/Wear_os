package com.fastaccess.provider.markdown;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;

import com.annimon.stream.IntStream;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.provider.timeline.HtmlHelper;

import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.ins.InsExtension;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Kosh on 24 Nov 2016, 7:43 PM
 */

public class MarkDownProvider {

    private static final String[] IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".gif", ".svg"};

    private static final String[] MARKDOWN_EXTENSIONS = {
            ".md", ".mkdn", ".mdwn", ".mdown", ".markdown", ".mkd", ".mkdown", ".ron", ".rst", "adoc"
    };

    private static final String[] ARCHIVE_EXTENSIONS = {
            ".zip", ".7z", ".rar", ".tar.gz", ".tgz", ".tar.Z", ".tar.bz2", ".tbz2", ".tar.lzma", ".tlz", ".apk", ".jar", ".dmg"
    };

    private MarkDownProvider() {}

    public static void setMdText(@NonNull TextView textView, String markdown) {
        if (!InputHelper.isEmpty(markdown)) {
            Parser parser = Parser.builder().build();
            Node node = parser.parse(markdown);
            String rendered = HtmlRenderer.builder()
                    .extensions(Arrays.asList(AutolinkExtension.create(),
                            StrikethroughExtension.create(),
                            TablesExtension.create(),
                            InsExtension.create(),
                            YamlFrontMatterExtension.create()))
                    .build().render(node);
            Logger.e(rendered);
            HtmlHelper.htmlIntoTextView(textView, rendered);
        }
    }

    public static void stripMdText(@NonNull TextView textView, String markdown) {
        if (!InputHelper.isEmpty(markdown)) {
            Parser parser = Parser.builder().build();
            Node node = parser.parse(markdown);
            textView.setText(stripHtml(HtmlRenderer.builder().build().render(node)));
        }
    }

    private static String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }

    public static void addList(@NonNull EditText editText, @NonNull String list) {
        String tag = list + " ";
        String source = editText.getText().toString();
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        String substring = source.substring(0, selectionStart);
        int line = substring.lastIndexOf(10);
        if (line != -1) {
            selectionStart = line + 1;
        } else {
            selectionStart = 0;
        }
        substring = source.substring(selectionStart, selectionEnd);
        String[] split = substring.split("\n");
        StringBuilder stringBuffer = new StringBuilder();
        if (split.length > 0)
            for (String s : split) {
                if (s.length() == 0 && stringBuffer.length() != 0) {
                    stringBuffer.append("\n");
                    continue;
                }
                if (!s.trim().startsWith(tag)) {
                    if (stringBuffer.length() > 0) stringBuffer.append("\n");
                    stringBuffer.append(tag).append(s);
                } else {
                    if (stringBuffer.length() > 0) stringBuffer.append("\n");
                    stringBuffer.append(s);
                }
            }

        if (stringBuffer.length() == 0) {
            stringBuffer.append(tag);
        }
        editText.getText().replace(selectionStart, selectionEnd, stringBuffer.toString());
        editText.setSelection(stringBuffer.length() + selectionStart);
    }

    public static void addHeader(@NonNull EditText editText, int level) {
        String source = editText.getText().toString();
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        StringBuilder result = new StringBuilder();
        String substring = source.substring(selectionStart, selectionEnd);
        if (!hasNewLine(source, selectionStart))
            result.append("\n");
        IntStream.range(0, level).forEach(integer -> result.append("#"));
        result.append(" ").append(substring);
        editText.getText().replace(selectionStart, selectionEnd, result.toString());
        editText.setSelection(selectionStart + result.length());

    }

    public static void addItalic(@NonNull EditText editText) {
        String source = editText.getText().toString();
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        String substring = source.substring(selectionStart, selectionEnd);
        String result = "_" + substring + "_ ";
        editText.getText().replace(selectionStart, selectionEnd, result);
        editText.setSelection(result.length() + selectionStart - 2);

    }

    public static void addBold(@NonNull EditText editText) {
        String source = editText.getText().toString();
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        String substring = source.substring(selectionStart, selectionEnd);
        String result = "**" + substring + "** ";
        editText.getText().replace(selectionStart, selectionEnd, result);
        editText.setSelection(result.length() + selectionStart - 3);

    }

    public static void addCode(@NonNull EditText editText) {
        try {
            String source = editText.getText().toString();
            int selectionStart = editText.getSelectionStart();
            int selectionEnd = editText.getSelectionEnd();
            String substring = source.substring(selectionStart, selectionEnd);
            String result;
            if (hasNewLine(source, selectionStart))
                result = "```\n" + substring + "\n```\n";
            else
                result = "\n```\n" + substring + "\n```\n";

            editText.getText().replace(selectionStart, selectionEnd, result);
            editText.setSelection(result.length() + selectionStart - 5);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addStrikeThrough(@NonNull EditText editText) {
        String source = editText.getText().toString();
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        String substring = source.substring(selectionStart, selectionEnd);
        String result = "~~" + substring + "~~ ";
        editText.getText().replace(selectionStart, selectionEnd, result);
        editText.setSelection(result.length() + selectionStart - 3);

    }

    public static void addQuote(@NonNull EditText editText) {
        String source = editText.getText().toString();
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        String substring = source.substring(selectionStart, selectionEnd);
        String result;
        if (hasNewLine(source, selectionStart)) {
            result = "> " + substring;
        } else {
            result = "\n> " + substring;

        }
        editText.getText().replace(selectionStart, selectionEnd, result);
        editText.setSelection(result.length() + selectionStart);

    }

    public static void addDivider(@NonNull EditText editText) {
        String source = editText.getText().toString();
        int selectionStart = editText.getSelectionStart();
        String result;
        if (hasNewLine(source, selectionStart)) {
            result = "-------\n";
        } else {
            result = "\n-------\n";
        }
        editText.getText().replace(selectionStart, selectionStart, result);
        editText.setSelection(result.length() + selectionStart);

    }

    public static void addPhoto(@NonNull EditText editText) {
        addLink(editText, "", "");
    }

    public static void addPhoto(@NonNull EditText editText, @NonNull String title, @NonNull String link) {
        String result = "![" + InputHelper.toString(title) + "](" + InputHelper.toString(link) + ")\n";
        String text = InputHelper.toString(editText);
        text += result;
        editText.setText(text);
        editText.setSelection(text.length());
    }

    public static void addLink(@NonNull EditText editText) {
        addLink(editText, "", "");
    }

    public static void addLink(@NonNull EditText editText, @NonNull String title, @NonNull String link) {
        String result = "[" + InputHelper.toString(title) + "](" + InputHelper.toString(link) + ")\n";
        String text = InputHelper.toString(editText);
        text += result;
        editText.setText(text);
        editText.setSelection(text.length());
    }

    private static boolean hasNewLine(@NonNull String source, int selectionStart) {
        try {
            if (source.isEmpty()) return true;
            source = source.substring(0, selectionStart);
            return source.charAt(source.length() - 1) == 10;
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isImage(@Nullable String name) {
        if (InputHelper.isEmpty(name)) return false;
        name = name.toLowerCase();
        for (String value : IMAGE_EXTENSIONS) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(name);
            if ((extension != null && value.replace(".", "").equals(extension)) || name.endsWith(value)) return true;
        }
        return false;
    }

    public static boolean isMarkdown(@Nullable String name) {
        if (InputHelper.isEmpty(name)) return false;
        name = name.toLowerCase();
        for (String value : MARKDOWN_EXTENSIONS) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(name);
            if ((extension != null && value.replace(".", "").equals(extension)) ||
                    name.equalsIgnoreCase("README") || name.endsWith(value))
                return true;
        }
        return false;
    }

    public static boolean isArchive(@Nullable String name) {
        if (InputHelper.isEmpty(name)) return false;
        name = name.toLowerCase();
        for (String value : ARCHIVE_EXTENSIONS) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(name);
            if ((extension != null && value.replace(".", "").equals(extension)) || name.endsWith(value)) return true;
        }

        return false;
    }

    private static class IndentedCodeBlockNodeRenderer implements NodeRenderer {

        private final HtmlWriter html;

        IndentedCodeBlockNodeRenderer(HtmlNodeRendererContext context) {
            this.html = context.getWriter();
        }

        @Override public Set<Class<? extends Node>> getNodeTypes() {
            return Collections.singleton(IndentedCodeBlock.class);
        }

        @Override public void render(Node node) {
            IndentedCodeBlock codeBlock = (IndentedCodeBlock) node;
            html.line();
            html.tag("pre");
            html.text(codeBlock.getLiteral());
            html.tag("/pre");
            html.line();
        }
    }
}
