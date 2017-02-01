package com.capgemini.cobigen.htmlplugin.merger;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.htmlplugin.merger.utils.ng2.ConstantsNG2;

/**
 * The {@link HTMLMerger} merges a patch and the base file of the same HTML file.
 *
 */
public class HTMLMerger implements Merger {

    /**
     * Merger Type to be registered
     */
    private String type;

    /**
     * The conflict resolving mode
     */
    private boolean patchOverrides;

    /**
     * Creates a new {@link HTMLMerger}
     *
     * @param type
     *            merger type
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    public HTMLMerger(String type, boolean patchOverrides) {

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

        try {
            fileDocBase = Jsoup.parse(base, targetCharset);
            docPatch = Jsoup.parse(patch, targetCharset);
            fixNGIFAtt(fileDocBase);
            Elements patchElements = docPatch.body().getAllElements();

            for (Element element : patchElements) {

                switch (element.tagName()) {
                case ConstantsNG2.MD_NAV_LIST:
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
                    break;
                case ConstantsNG2.FORM:
                    if (element.id().equals("")) {
                        Element filterForm = fileDocBase.getElementsByTag(ConstantsNG2.FORM).first();
                        if (patchOverrides) {
                            for (Element input : filterForm.getElementsByTag(ConstantsNG2.INPUT_CONTAINER)) {
                                input.remove();
                            }
                            filterForm.insertChildren(0, docPatch.getElementsByTag(ConstantsNG2.FORM).first()
                                .getElementsByTag(ConstantsNG2.INPUT_CONTAINER));
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
                    } else {
                        Element filterForm = fileDocBase.getElementsByTag(ConstantsNG2.FORM).first();
                        if (patchOverrides) {
                            for (Element input : filterForm.getElementsByTag(ConstantsNG2.INPUT_CONTAINER)) {
                                input.remove();
                            }
                            filterForm.insertChildren(0, docPatch.getElementsByTag(ConstantsNG2.FORM).first()
                                .getElementsByTag(ConstantsNG2.INPUT_CONTAINER));
                        } else {
                            Elements filterFieldsBase = fileDocBase.getElementsByTag(ConstantsNG2.INPUT);
                            List<String> fieldNames = getFilterInputsNames(filterFieldsBase);
                            Elements filterFieldsPatch = docPatch.getElementsByTag(ConstantsNG2.INPUT);
                            List<Node> toAdd = new LinkedList<>();
                            for (Element input : filterFieldsPatch) {
                                if (!fieldNames.contains(input.attr(ConstantsNG2.NAME_ATTR))) {
                                    toAdd.add(new Element(ConstantsNG2.INPUT_CONTAINER).attr("style", "width:100%")
                                        .appendChild(input));
                                }
                            }
                            if (!toAdd.isEmpty()) {
                                filterForm.insertChildren(fieldNames.size() * 2, toAdd);
                                System.out.println(filterForm);
                            }
                        }
                    }

                    break;
                }

            }
        } catch (IOException e) {
            throw new MergeException(base, "file could not be found, or read, or the charsetName is invalid");
        }

        return String.valueOf(fileDocBase);
    }

    public boolean isPatchOverrides() {
        return patchOverrides;
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

    public void fixNGIFAtt(Document base) {

        for (Element element : base.getAllElements()) {
            if (element.hasAttr("*ngIf")) {
                String value = element.attributes().get("*ngIf");
                element.attributes().remove("*ngIf");
                element.attributes().put("*ngIf", value);
            }
        }
    }
}
