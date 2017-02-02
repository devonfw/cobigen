package com.capgemini.cobigen.htmlplugin.merger.ng2;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.capgemini.cobigen.htmlplugin.merger.ng2.utils.constants.ConstantsNG2;

/**
 * The {@link Angular2Merger} merges HTML contents needed for client generation
 */
public class Angular2Merger {

    /**
     * Existent HTML {@link Document}
     */
    private Document fileDocBase;

    /**
     * HTML {@link Document} that patches the existent
     */
    private Document docPatch;

    @SuppressWarnings("javadoc")
    public Angular2Merger(Document fileDocBase, Document docPatch) {
        this.fileDocBase = fileDocBase;
        this.docPatch = docPatch;
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
        Element sideMenuBase = fileDocBase.getElementsByTag(ConstantsNG2.MD_NAV_LIST).first();
        if (patchOverrides) {
            sideMenuBase.replaceWith(element);
        } else {
            Elements patchButtons = element.getElementsByTag(ConstantsNG2.A_REF);
            List<String> idBase = getIds(sideMenuBase.getElementsByTag(ConstantsNG2.A_REF));
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
        Element filterForm = fileDocBase.getElementsByTag(ConstantsNG2.FORM).first();
        if (patchOverrides) {
            for (Element input : filterForm.getElementsByTag(ConstantsNG2.INPUT_CONTAINER)) {
                input.remove();
            }
            filterForm.insertChildren(0,
                docPatch.getElementsByTag(ConstantsNG2.FORM).first().getElementsByTag(ConstantsNG2.INPUT_CONTAINER));
        } else {
            Elements filterFieldsBase = fileDocBase.getElementsByTag(ConstantsNG2.INPUT);
            List<String> fieldNames = getFilterInputsNames(filterFieldsBase);
            Elements filterFieldsPatch = docPatch.getElementsByTag(ConstantsNG2.INPUT);
            List<Node> toAdd = new LinkedList<>();
            for (Element input : filterFieldsPatch) {
                if (!fieldNames.contains(input.attr(ConstantsNG2.NAME_ATTR))) {
                    toAdd.add(new Element(ConstantsNG2.INPUT_CONTAINER).appendChild(input));
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
        Element filterForm = fileDocBase.getElementsByTag(ConstantsNG2.FORM).first();
        if (patchOverrides) {
            for (Element input : filterForm.getElementsByTag(ConstantsNG2.INPUT_CONTAINER)) {
                input.remove();
            }
            filterForm.insertChildren(0,
                docPatch.getElementsByTag(ConstantsNG2.FORM).first().getElementsByTag(ConstantsNG2.INPUT_CONTAINER));
        } else {
            Elements filterFieldsBase = fileDocBase.getElementsByTag(ConstantsNG2.INPUT);
            List<String> fieldNames = getFilterInputsNames(filterFieldsBase);
            Elements filterFieldsPatch = docPatch.getElementsByTag(ConstantsNG2.INPUT);
            List<Node> toAdd = new LinkedList<>();
            for (Element input : filterFieldsPatch) {
                if (!fieldNames.contains(input.attr(ConstantsNG2.NAME_ATTR))) {
                    toAdd.add(new Element(ConstantsNG2.INPUT_CONTAINER).attr("style", "width:100%").appendChild(input));
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
            names.add(input.attr(ConstantsNG2.NAME_ATTR));
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
            if (element.hasAttr(ConstantsNG2.NGIF_ATRR)) {
                String value = element.attributes().get(ConstantsNG2.NGIF_ATRR);
                element.attributes().remove(ConstantsNG2.NGIF_ATRR);
                element.attributes().put(ConstantsNG2.NGIF_ATRR, value);
            }
        }
    }

    /**
     * Chooses the method for the merge
     *
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     * @return the string resulting of the merger
     */
    public String merger(boolean patchOverrides) {
        Elements patchElements = docPatch.body().getAllElements();
        fixNGIFAtt(fileDocBase);
        for (Element element : patchElements) {

            switch (element.tagName()) {
            case ConstantsNG2.MD_NAV_LIST:
                AppComponent(fileDocBase, element, patchOverrides);
                break;
            case ConstantsNG2.FORM:
                if (element.id().isEmpty()) {
                    dataGrid(fileDocBase, docPatch, patchOverrides);
                } else {
                    addDialog(fileDocBase, docPatch, patchOverrides);
                }
                break;
            }

        }
        return String.valueOf(fileDocBase);
    }

}
