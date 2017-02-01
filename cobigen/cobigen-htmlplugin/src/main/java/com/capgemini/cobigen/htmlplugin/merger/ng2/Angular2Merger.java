package com.capgemini.cobigen.htmlplugin.merger.ng2;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.capgemini.cobigen.htmlplugin.merger.ng2.utils.constants.ConstantsNG2;

/**
 *
 */
public class Angular2Merger {

    private Document fileDocBase;

    private Document docPatch;

    /**
     * @param fileDocBase
     * @param docPatch
     * @param patchOverrides
     * @param targetCharset
     */
    public Angular2Merger(Document fileDocBase, Document docPatch) {
        this.fileDocBase = fileDocBase;
        this.docPatch = docPatch;
    }

    public void AppComponent(Document fileDocBase, Element element, boolean patchOverrides) {
        Element sideMenuBase = fileDocBase.getElementsByTag(ConstantsNG2.MD_NAV_LIST).first();
        if (patchOverrides) {
            sideMenuBase.replaceWith(element);
        } else {
            Elements patchButtons = element.getElementsByTag(ConstantsNG2.A_REF);
            List<String> idBase = getIds(sideMenuBase.getElementsByTag(ConstantsNG2.A_REF));
            for (Element button : patchButtons) {
                if (!idBase.contains(button.id()) && !button.id().equals("")) {
                    sideMenuBase.appendChild(button);
                }
            }
        }
    }

    public void dataGrid(Document fileDocBase, Document docPatch, Element element, boolean patchOverrides) {
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

    public void addDialog(Document fileDocBase, Document docPatch, Element element, boolean patchOverrides) {
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

    public List<String> getFilterInputsNames(Elements inputs) {
        List<String> names = new LinkedList<>();
        for (Element input : inputs) {
            names.add(input.attr("name"));
        }
        return names;
    }

    public List<String> getIds(Elements elements) {

        List<String> ids = new LinkedList<>();
        for (Element elem : elements) {
            if (!elem.id().equals("")) {
                ids.add(elem.id());
            }

        }
        return ids;
    }

    public void fixNGIFAtt(Document fileDocBase) {

        for (Element element : fileDocBase.getAllElements()) {
            if (element.hasAttr("*ngIf")) {
                String value = element.attributes().get("*ngIf");
                element.attributes().remove("*ngIf");
                element.attributes().put("*ngIf", value);
            }
        }
    }

    /**
     * @param fileDocBase
     * @param docPatch
     * @param patchOverrides
     * @param targetCharset
     * @return
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
                if (element.id().equals("")) {
                    dataGrid(fileDocBase, docPatch, element, patchOverrides);
                } else {
                    addDialog(fileDocBase, docPatch, element, patchOverrides);
                }
                break;
            }

        }
        return String.valueOf(fileDocBase);
    }

}
