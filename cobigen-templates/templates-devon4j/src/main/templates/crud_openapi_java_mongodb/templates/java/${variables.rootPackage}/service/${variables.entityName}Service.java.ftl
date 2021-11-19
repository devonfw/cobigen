package ${variables.rootPackage}.service;

import ${variables.rootPackage}.model.${variables.entityName};
import ${variables.rootPackage}.repository.${variables.entityName}Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ${variables.entityName}Service {

    private ${variables.entityName}Repository ${variables.entityName?lower_case}Repository;

    @Autowired
    public ${variables.entityName}Service(${variables.entityName}Repository ${variables.entityName?lower_case}Repository) {
        this.${variables.entityName?lower_case}Repository = ${variables.entityName?lower_case}Repository;
    }

    public ${variables.entityName} save${variables.entityName}(${variables.entityName} ${variables.entityName?lower_case}){
        return ${variables.entityName?lower_case}Repository.save(${variables.entityName?lower_case});
    }

    public Iterable<${variables.entityName}> getAll${variables.entityName}s(){
        return ${variables.entityName?lower_case}Repository.findAll();
    }

    public void deleteAll${variables.entityName}s(){
        ${variables.entityName?lower_case}Repository.deleteAll();
    }

    public void delete${variables.entityName}ById(String id){
        ${variables.entityName?lower_case}Repository.deleteById(id);
    }

    public Optional<${variables.entityName}> find${variables.entityName}ById(String id){
        return ${variables.entityName?lower_case}Repository.findById(id);
    }
}
