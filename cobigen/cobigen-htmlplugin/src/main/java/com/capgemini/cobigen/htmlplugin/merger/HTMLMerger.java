package com.capgemini.cobigen.htmlplugin.merger;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.htmlplugin.merger.utils.ConstantsNG2;

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

        patch = "<header></header>" + "<md-sidenav-layout style=\"height:91vh\">"
            + "<md-sidenav *ngIf=\"this.router.location.path() !== '/login'\" #sidenav style= \"width:25%\" mode=\"side\" opened=\"true\" class=\"app-sidenav\">"
            + "<span class=\"app-toolbar-filler\"></span>" + "<md-nav-list list-items>"
            + "<a id=\"home\" md-list-item (click)=\"navigateTo('home')\">" + "<md-icon md-list-avatar>home</md-icon>"
            + "<h3 md-line> {{'datagrid.navHome' | translate}} </h3>"
            + "<p md-line> {{'datagrid.navHomeSub' | translate}} </p>" + "</a>"
            + "<a id=\"newDataGrid\" md-list-item (click)=\"navigateTo('newDataGrid')\">"
            + "<md-icon md-list-avatar>grid_on</md-icon>" + "<h3 md-line> {{'newDatagrid.navData' | translate}} </h3>"
            + "<p md-line> {{'newDatagrid.navDataSub' | translate}} </p>" + "</a>" + "</md-nav-list>" + "</md-sidenav>"
            + "<router-outlet></router-outlet>" + "</md-sidenav-layout>";
        Document fileDocBase;
        Document docPatch;

        try {
            fileDocBase = Jsoup.parse(base, "UTF-8");
            docPatch = Jsoup.parse(patch, "UTF-8");

            fixNGIFAtt(fileDocBase);
            Elements patchElements = docPatch.getAllElements();

            for (Element element : patchElements) {

                switch (element.tagName()) {
                case ConstantsNG2.MD_NAV_LIST:
                    Element sideMenuBase = fileDocBase.getElementsByTag(ConstantsNG2.MD_NAV_LIST).get(0);
                    Element sideMenuPatch = element.getElementsByTag(ConstantsNG2.MD_NAV_LIST).get(0);
                    if (patchOverrides) {
                        sideMenuBase.replaceWith(sideMenuPatch);
                    } else {
                        Elements patchButtons = sideMenuPatch.getElementsByTag(ConstantsNG2.A_REF);
                        List<String> idBase = getIds(sideMenuBase.getElementsByTag(ConstantsNG2.A_REF));
                        for (Element button : patchButtons) {
                            if (!idBase.contains(button.id()) && !button.id().equals("")) {
                                sideMenuBase.appendChild(button);
                            }
                        }
                    }
                    break;
                case ConstantsNG2.TABLE_DATA:
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
