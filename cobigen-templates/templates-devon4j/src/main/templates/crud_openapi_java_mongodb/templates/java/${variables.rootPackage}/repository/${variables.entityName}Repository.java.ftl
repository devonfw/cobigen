package ${variables.rootPackage}.repository;


import ${variables.rootPackage}.model.${variables.entityName};
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ${variables.entityName}Repository extends CrudRepository<${variables.entityName}, String> {
}
