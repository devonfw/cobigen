package com.devonfw.cobigen.htmlplugin.merger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.htmlplugin.merger.constants.Constants;

/**
 * The {@link AngularMerger} merges a patch and the base file of the same HTML file.
 *
 */
public class AngularMerger implements Merger {

    /**
     * Merger Type to be registered
     */
    private String type;

    /**
     * The conflict resolving mode
     */
    private boolean patchOverrides;

    /**
     * Creates a new {@link AngularMerger}
     *
     * @param type
     *            merger type
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    public AngularMerger(String type, boolean patchOverrides) {

        this.type = type;
        this.patchOverrides = patchOverrides;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String merge(File base, String patch, String targetCharset) throws MergeException {

        Document fileDocBase;
        Document docPatch;
        String htmlString;
        Parser parse = Parser.htmlParser();
        parse.settings(new ParseSettings(true, true));
        try (Reader reader = new FileReader(base)) {
            htmlString = IOUtils.toString(reader);
        } catch (IOException e) {
            throw new MergeException(base, "file could not be found, or read, or the charsetName is invalid");
        }

        fileDocBase = parse.parseInput(htmlString, base.toString());
        docPatch = parse.parseInput(patch, targetCharset);
        Elements patchElements = docPatch.body().getAllElements();
        fixNGIFAtt(fileDocBase);
        for (Element element : patchElements) {

            switch (element.tagName()) {
            case Constants.MD_NAV_LIST:
                AppComponent(fileDocBase, element, patchOverrides);
                break;
            case Constants.FORM:
                if (element.id().isEmpty()) {
                    dataGrid(fileDocBase, docPatch, patchOverrides);
                } else {
                    addDialog(fileDocBase, docPatch, patchOverrides);
                }
                break;
            }

        }

        return fileDocBase.body().children().toString();
    }

    /**
     * Method to merge the app.component.html file
     *
     * @param fileDocBase
     *            the existent app.component.html
     * @param element
     *            the nav-list {@link Element}
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    public void AppComponent(Document fileDocBase, Element element, boolean patchOverrides) {
        Element sideMenuBase = fileDocBase.getElementsByTag(Constants.MD_NAV_LIST).first();
        if (patchOverrides) {
            sideMenuBase.replaceWith(element);
        } else {
            Elements patchButtons = element.getElementsByTag(Constants.A_REF);
            List<String> idBase = getIds(sideMenuBase.getElementsByTag(Constants.A_REF));
            for (Element button : patchButtons) {
                if (!idBase.contains(button.id()) && !button.id().isEmpty()) {
                    sideMenuBase.appendChild(button);
                }
            }
        }
    }

    /**
     * Method to merge the dataGrid.component.html file
     *
     * @param fileDocBase
     *            the existent dataGrid.component.html
     * @param docPatch
     *            the {@link Document} that patches
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    public void dataGrid(Document fileDocBase, Document docPatch, boolean patchOverrides) {
        Element filterForm = fileDocBase.getElementsByTag(Constants.FORM).first();
        if (patchOverrides) {
            for (Element input : filterForm.getElementsByTag(Constants.INPUT_CONTAINER)) {
                input.remove();
            }
            filterForm.insertChildren(0,
                docPatch.getElementsByTag(Constants.FORM).first().getElementsByTag(Constants.INPUT_CONTAINER));
        } else {
            Elements filterFieldsBase = fileDocBase.getElementsByTag(Constants.INPUT);
            List<String> fieldNames = getFilterInputsNames(filterFieldsBase);
            Elements filterFieldsPatch = docPatch.getElementsByTag(Constants.INPUT);
            List<Node> toAdd = new LinkedList<>();
            for (Element input : filterFieldsPatch) {
                if (!fieldNames.contains(input.attr(Constants.NAME_ATTR))) {
                    toAdd.add(new Element(Constants.INPUT_CONTAINER).appendChild(input));
                }
            }
            if (!toAdd.isEmpty()) {
                filterForm.insertChildren(fieldNames.size() * 2, toAdd);
            }
        }
    }

    /**
     * Method to merge the addDialog.component.html file
     *
     * @param fileDocBase
     *            the existent dataGrid.component.html
     * @param docPatch
     *            the {@link Document} that patches
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    public void addDialog(Document fileDocBase, Document docPatch, boolean patchOverrides) {
        Element filterForm = fileDocBase.getElementsByTag(Constants.FORM).first();
        if (patchOverrides) {
            for (Element input : filterForm.getElementsByTag(Constants.INPUT_CONTAINER)) {
                input.remove();
            }
            filterForm.insertChildren(0,
                docPatch.getElementsByTag(Constants.FORM).first().getElementsByTag(Constants.INPUT_CONTAINER));
        } else {
            Elements filterFieldsBase = fileDocBase.getElementsByTag(Constants.INPUT);
            List<String> fieldNames = getFilterInputsNames(filterFieldsBase);
            Elements filterFieldsPatch = docPatch.getElementsByTag(Constants.INPUT);
            List<Node> toAdd = new LinkedList<>();
            for (Element input : filterFieldsPatch) {
                if (!fieldNames.contains(input.attr(Constants.NAME_ATTR))) {
                    toAdd.add(new Element(Constants.INPUT_CONTAINER).attr("style", "width:100%").appendChild(input));
                }
            }
            if (!toAdd.isEmpty()) {
                filterForm.insertChildren(fieldNames.size() * 2, toAdd);
            }
        }
    }

    /**
     * Returns a list of values of name attributes of a group of {@link Elements}
     *
     * @param inputs
     *            the elements to extract the the values
     * @return the list of values
     */
    public List<String> getFilterInputsNames(Elements inputs) {
        List<String> names = new LinkedList<>();
        for (Element input : inputs) {
            names.add(input.attr(Constants.NAME_ATTR));
        }
        return names;
    }

    /**
     * Returns a list of values of ID attributes of a group of {@link Elements}
     *
     * @param elements
     *            the group of {@link Elements}
     * @return the list of ID values
     */
    public List<String> getIds(Elements elements) {

        List<String> ids = new LinkedList<>();
        for (Element elem : elements) {
            if (!elem.id().isEmpty()) {
                ids.add(elem.id());
            }

        }
        return ids;
    }

    /**
     * The *ngIf attribute is parsed as lower case, causing errors on the compilation of the Angular2 client.
     * This method fixes this attribute
     *
     * @param fileDocBase
     *            the existent {@link Document}
     */
    public void fixNGIFAtt(Document fileDocBase) {

        for (Element element : fileDocBase.getAllElements()) {
            if (element.hasAttr(Constants.NGIF_ATRR)) {
                String value = element.attributes().get(Constants.NGIF_ATRR);
                element.attributes().remove(Constants.NGIF_ATRR);
                element.attributes().put(Constants.NGIF_ATRR, value);
            }
        }
    }

}
