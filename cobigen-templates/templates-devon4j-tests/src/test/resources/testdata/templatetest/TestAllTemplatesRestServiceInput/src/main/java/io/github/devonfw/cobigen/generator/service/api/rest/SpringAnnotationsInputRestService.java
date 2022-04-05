package io.github.devonfw.cobigen.generator.service.api.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/test/v1")
public interface SpringAnnotationsInputRestService {

    /**
     * Delegates to nothing.
     *
     * @param id
     *            the ID of nothing
     * @return nothing really
     */
    @RequestMapping(path = "/test1/{id}/", method = RequestMethod.GET)
    public String getVisitor(long id);

    /**
     * Delegates to nothing.
     *
     * @param nothing
     *            actually
     * @return apparently a String
     */
    @RequestMapping(path = "/test/", method = RequestMethod.POST)
    public String saveVisitor(String test);

    /**
     * Delegates to nothing.
     *
     * @param id
     *            ID of nothing to be deleted
     */
    @RequestMapping(path = "/test2/{id}/", method = RequestMethod.DELETE)
    public void deleteVisitor(long id);

}