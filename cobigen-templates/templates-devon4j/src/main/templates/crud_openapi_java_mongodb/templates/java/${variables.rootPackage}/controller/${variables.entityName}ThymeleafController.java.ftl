package ${variables.rootPackage}.controller;

import ${variables.rootPackage}.model.${variables.entityName};
import ${variables.rootPackage}.service.${variables.entityName}Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class ${variables.entityName}ThymeleafController {

    private ${variables.entityName}Service ${variables.entityName?lower_case}Service;

    Logger logger = LoggerFactory.getLogger(${variables.entityName}Service.class);

    @Autowired
    public ${variables.entityName}ThymeleafController(${variables.entityName}Service ${variables.entityName?lower_case}Service) {
        this.${variables.entityName?lower_case}Service = ${variables.entityName?lower_case}Service;
    }

    @GetMapping("/${variables.entityName?lower_case}s-ui")
    public String ${variables.entityName?lower_case}s(Model model) {
        model.addAttribute("${variables.entityName?lower_case}s", ${variables.entityName?lower_case}Service.getAll${variables.entityName}s());
        logger.debug("ALL ${variables.entityName?upper_case}S DONE");
        return "${variables.entityName?lower_case}s";
    }

    @GetMapping("/delete-${variables.entityName?lower_case}/{id}")
    public String remove${variables.entityName}(@PathVariable("id") String id, Model model) {
        ${variables.entityName?lower_case}Service.delete${variables.entityName}ById(id);
        model.addAttribute("${variables.entityName?lower_case}s", ${variables.entityName?lower_case}Service.getAll${variables.entityName}s());
        logger.debug("DELETE ${variables.entityName?upper_case} DONE");
        return "${variables.entityName?lower_case}s";
    }

    @GetMapping(value = {"/edit-add-${variables.entityName?lower_case}/{id}", "/edit-add-${variables.entityName?lower_case}"})
    public String edit${variables.entityName}(@PathVariable("id") Optional<String> id, Model model) {
        ${variables.entityName} ${variables.entityName?lower_case} = id.isPresent() ?
                ${variables.entityName?lower_case}Service.find${variables.entityName}ById(id.get()).get() : new ${variables.entityName}();
        model.addAttribute("${variables.entityName?lower_case}", ${variables.entityName?lower_case});
        logger.debug("EDIT ${variables.entityName?upper_case} DONE");
        return "add-edit";
    }

    @PostMapping("/save-${variables.entityName?lower_case}")
    public String save${variables.entityName}(@ModelAttribute("${variables.entityName?lower_case}") @Valid ${variables.entityName} ${variables.entityName?lower_case},
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "add-edit";
        }
        ${variables.entityName?lower_case}Service.save${variables.entityName}(${variables.entityName?lower_case});
        logger.debug("SAVED ${variables.entityName?upper_case} DONE");
        return "redirect:${variables.entityName?lower_case}s-ui";
    }
}
