package com.devonfw.cobigen.api.template.provider;

import java.util.Collection;

import com.devonfw.cobigen.api.model.CobiGenModel;

/**
 * Provider of a {@link Collection} from the {@link CobiGenModel}.
 */
public interface CobiGenCollectionProvider {

  Collection<?> get(CobiGenModel model);

}
