package com.devonfw.cobigen.api.template.mapper;

import com.devonfw.cobigen.api.model.CobiGenModel;

/**
 * Interface for a mapper to filter or transform a {@link CobiGenModel} from a
 * {@link com.devonfw.cobigen.api.template.provider.CobiGenCollectionProvider provider} such as
 * {@link com.devonfw.cobigen.api.template.provider.JavaFieldModel}.
 */
public interface CobiGenModelMapper {

  /**
   * @param model the {@link CobiGenModel} to map (filter or transform).
   * @return the given {@link CobiGenModel} to keep unchanged, a transformed {@link CobiGenModel} or {@code null} to
   *         filter and ignore.
   */
  CobiGenModel map(CobiGenModel model);

}
