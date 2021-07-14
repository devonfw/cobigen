package io.github.maybeec.html2text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class HtmlProcessor {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(HtmlProcessor.class);

    public String process(Path input, boolean htmlEscape) throws IOException {
        Document doc = Jsoup.parse(input.toFile(), StandardCharsets.UTF_8.toString());
        StringBuilder sb = new StringBuilder();
        process(doc, sb, htmlEscape);
        return sb.toString();
    }

    private void process(Node element, StringBuilder sb, boolean htmlEscape) {
        for (Node elem : element.childNodes()) {
            if (elem instanceof Element) {
                switch (elem.nodeName()) {
                case "p":
                    process(elem, sb, htmlEscape);
                    sb.append(System.lineSeparator());
                    break;
                case "h1":
                case "h2":
                    StringBuilder headlineB = new StringBuilder();
                    process(elem, headlineB, htmlEscape);
                    String headline = headlineB.toString();
                    sb.append(System.lineSeparator());
                    sb.append(headline);
                    sb.append(System.lineSeparator());
                    sb.append(StringUtils.repeat('=', headline.length()));
                    sb.append(System.lineSeparator());
                    sb.append(System.lineSeparator());
                    break;
                case "h3":
                    headlineB = new StringBuilder();
                    process(elem, headlineB, htmlEscape);
                    headline = headlineB.toString();
                    sb.append(System.lineSeparator());
                    sb.append(headline);
                    sb.append(System.lineSeparator());
                    sb.append(StringUtils.repeat('-', headline.length()));
                    sb.append(System.lineSeparator());
                    sb.append(System.lineSeparator());
                    break;
                case "tbody":
                    processTable(elem, sb, htmlEscape);
                    break;
                case "a":
                    process(elem, sb, htmlEscape);
                    sb.append(' ');
                    sb.append("(");
                    sb.append(elem.attr("href"));
                    sb.append(")");
                    break;
                case "ul":
                    sb.append(System.lineSeparator());
                    process(elem, sb, htmlEscape);
                    sb.append(System.lineSeparator());
                    break;
                case "li":
                    sb.append("  * ");
                    process(elem, sb, htmlEscape);
                    sb.append(System.lineSeparator());
                    break;
                case "hr":
                    sb.append(StringUtils.repeat('_', 254));
                    sb.append(System.lineSeparator());
                    break;
                case "pre":
                    sb.append(elem.childNodes().stream().filter(e -> e instanceof TextNode)
                        .map(e -> ((TextNode) e).getWholeText()).collect(Collectors.joining()));
                    break;
                default:
                    process(elem, sb, htmlEscape);
                }
            } else if (elem instanceof TextNode) {
                if (!((TextNode) elem).isBlank()) {
                    sb.append(((TextNode) elem).text());
                }
            } else {
                LOG.debug("Not processed element of type {}", elem.getClass());
            }
        }
    }

    private void processTable(Node elem, StringBuilder sb, boolean htmlEscape) {
        TableModel model = parseTable(elem, htmlEscape);
        String[][][] cells = model.getCells();

        sb.append('+');
        int tableWidth = Arrays.stream(model.getColumnWidth()).sum() + model.getColumnWidth().length * 2
            + model.getColumnWidth().length - 1;
        sb.append(StringUtils.repeat('-', tableWidth));
        sb.append('+');
        sb.append(System.lineSeparator());

        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[row].length; column++) {
                for (int line = 0; line < cells[row][column].length; line++) {
                    sb.append('|');
                    sb.append(' ');
                    sb.append(StringUtils.rightPad(cells[row][column][line], model.getColumnWidth()[column]));
                    sb.append(' ');
                    if (line != cells[row][column].length - 1) {
                        sb.append('|');
                        sb.append(System.lineSeparator());
                    }
                }
                if (column == cells[row].length - 1) {
                    sb.append('|');
                }
            }
            sb.append(System.lineSeparator());
            if (model.isHasHeadline() && row == 0) {
                sb.append('+');
                sb.append(StringUtils.repeat('-', tableWidth));
                sb.append('+');
                sb.append(System.lineSeparator());
            }
        }

        sb.append('+');
        sb.append(StringUtils.repeat('-', tableWidth));
        sb.append('+');
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
    }

    /**
     * @param elem
     * @return
     */
    private TableModel parseTable(Node elem, boolean htmlEscape) {
        int[] columnWidth = null;
        int rowCount = (int) elem.childNodes().stream().filter(e -> !(e instanceof Element)).count();
        String[][][] cells = new String[rowCount][][];
        int[] rowHeight = new int[rowCount];
        int row = 0;
        boolean hasHeadline = false;
        for (Node tr : elem.childNodes()) {
            if (!(tr instanceof Element)) {
                continue;
            }
            int column = 0;
            int columnCount = (int) tr.childNodes().stream().filter(e -> e instanceof Element).count();
            if (columnWidth == null) {
                columnWidth = new int[columnCount];
            }
            cells[row] = new String[columnWidth.length][];
            for (Node td : tr.childNodes()) {
                if (!(td instanceof Element)) {
                    continue;
                }
                hasHeadline = hasHeadline || td.nodeName().equals("th");
                StringBuilder builder = new StringBuilder();
                process(td, builder, htmlEscape);
                String trimmedCell = builder.toString().trim();
                if (htmlEscape) {
                    trimmedCell = trimmedCell.replace("<", "&lt;").replace(">", "&gt;");
                }
                cells[row][column] = trimmedCell.split("\r\n|\r|\n");
                rowHeight[row] = Math.max(rowHeight[row], cells[row][column].length);
                columnWidth[column] = Math.max(columnWidth[column],
                    Arrays.stream(cells[row][column]).map(String::length).max(Integer::compareTo).get());
                column++;
            }
            row++;
        }
        return new TableModel(columnWidth, cells, hasHeadline);
    }
}
