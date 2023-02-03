package x_rootpackage_x.x_component_x.common;

import com.devonfw.cobigen.api.annotation.CobiGenForEach;
import com.devonfw.cobigen.api.template.mapper.CobiGenModelMapperFieldsEto;
import com.devonfw.cobigen.api.template.provider.CobiGenCollectionProviderFields;
import com.google.common.base.Objects;

import x_rootpackage_x.general.common.AbstractEto;

/**
 * Implementation of {@link X_EntityName_X} as {@link AbstractEto ETO}.
 */
public class X_EntityName_XEto2 extends X_DynamicEntityNameEtoParent_X implements X_EntityName_X {

  @CobiGenForEach(value = CobiGenCollectionProviderFields.class, mapper = CobiGenModelMapperFieldsEto.class)
  private X_FieldType_X x_fieldName_x;

  /**
   * The constructor.
   */
  public X_EntityName_XEto2() {

    super();
  }

  /**
   * @return the x_fieldName_x.
   */
  @CobiGenForEach(value = CobiGenCollectionProviderFields.class, mapper = CobiGenModelMapperFieldsEto.class)
  public X_FieldType_X x_fieldgetterprefix_xX_FieldName_X() {

    return this.x_fieldName_x;
  }

  /**
   * @param x_fieldname_x the new value of {@link #x_fieldgetterprefix_xX_FieldName_X()}.
   */
  @CobiGenForEach(value = CobiGenCollectionProviderFields.class, mapper = CobiGenModelMapperFieldsEto.class)
  public void setX_FieldName_X(X_FieldType_X x_fieldname_x) {

    this.x_fieldName_x = x_fieldname_x;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == this) {
      return true;
    } else if (!super.equals(obj)) {
      return false;
    }
    X_EntityName_XEto2 other = (X_EntityName_XEto2) obj;
    @CobiGenForEach(value = CobiGenCollectionProviderFields.class, mapper = CobiGenModelMapperFieldsEto.class)
    int forEach;
    {
      if (!Objects.equal(this.x_fieldName_x, other)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {

    int hash = super.hashCode();
    @CobiGenForEach(value = CobiGenCollectionProviderFields.class, mapper = CobiGenModelMapperFieldsEto.class)
    int forEach;
    {
      hash = 31 * hash + Objects.hashCode(this.x_fieldName_x);

    }
    return hash;
  }

}
