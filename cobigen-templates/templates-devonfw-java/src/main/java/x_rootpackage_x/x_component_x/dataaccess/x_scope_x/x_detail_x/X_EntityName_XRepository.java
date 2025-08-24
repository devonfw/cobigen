package x_rootpackage_x.x_component_x.dataaccess.x_scope_x.x_detail_x;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devonfw.cobigen.api.annotation.CobiGenProperties;
import com.devonfw.cobigen.api.annotation.CobiGenProperty;
import com.devonfw.cobigen.api.annotation.CobiGenTemplate;
import com.devonfw.cobigen.api.template.CobiGenJavaIncrements;
import com.devonfw.cobigen.api.template.CobiGenJavaProperties;

/**
 * {@link JpaRepository} for {@link X_EntityName_XEntity}.
 */
@CobiGenTemplate(value = CobiGenJavaIncrements.DATA_ACCESS)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_API),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_CORE) })
public interface X_EntityName_XRepository extends JpaRepository<X_EntityName_XEntity, Long> {

}
