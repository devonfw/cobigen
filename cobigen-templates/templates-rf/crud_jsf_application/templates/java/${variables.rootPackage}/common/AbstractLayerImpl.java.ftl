package ${variables.rootPackage}.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dozer.Mapper;

/**
 * This is the abstract base class for every implementation class (every layer). The class provides the
 * functionality to map diferent layer entities.
 * @generated
 */
public class AbstractLayerImpl {

    protected final Logger LOG = Logger.getLogger(getClass());

    private Mapper dozer;

    /**
     * The constructor.
     */
    public AbstractLayerImpl() {

    }

    /**
     * @param dozer
     *            the {@link Mapper}
     */
    public void setDozer(Mapper dozer) {
        this.dozer = dozer;
    }

    /**
     * Maps an initial layer entity to a target layer entity. The resulting entity type has to be given.
     *
     * @param entity
     *            The initial layer entity
     * @param outputEntityType
     *            {@link Class} The target type P.
     * @param <C>
     *            Input type: Initial layer type (e.g. core class type)
     * @param <P>
     *            Output type: Target layer type (e.g. persistence entity type)
     * @return Mapped target layer entity P.
     */
    protected <C, P> P mapInitialToTargetLayerEntity(C entity, Class<P> outputEntityType) {

        if (entity == null) {
            LOG.debug("No entity to map.");
            return null;
        }

        LOG.debug("Mapping: '" + entity.getClass().getName() + "' --> '" + outputEntityType.getName() + "'.");

        return ((P) dozer.map(entity, outputEntityType));
    }

    /**
     * Maps a list of initial layer entities to a list of target layer entities. The resulting entity type has
     * to be given. The given type MUST be the resulting list type.
     *
     * @param entities
     *            The list of initial layer entities
     * @param outputEntityType
     *            {@link Class} The target type P.
     * @param <C>
     *            Input type: Initial layer type (e.g. core class type)
     * @param <P>
     *            Output type: Target layer type (e.g. persistence entity type)
     * @return A {@link List} of P entities
     */
    protected <C, P> List<P> mapInitialToTargetLayerEntity(List<C> entities, Class<P> outputEntityType) {
        if (entities == null || entities.isEmpty()) {
            LOG.debug("No entities to map.");
            return null;
        }

        List<P> targetEntities = new ArrayList<P>();

        for (C entity : entities) {
            targetEntities.add(this.mapInitialToTargetLayerEntity(entity, outputEntityType));
        }
        return targetEntities;
    }

    /**
     * Determines the sub type for a given super entity (both at the initial layer) and maps this casted sub
     * type to the targets layer equivalent type. If there is no 'super <-> sub' relationship, this method
     * returns null.
     *
     * This method has to be chosen if a mapping should be done via dozer. The 'sub <-> super' relationship
     * won't be established by that mapping.
     *
     * This method preserves this relationship.
     *
     * @param initialLayerSuperClassEntity
     *            The initial layers super entity (e.g. persistence super type)
     * @param initialLayerSubClassType
     *            The initial layers class sub type (e.g. persistence layer)
     * @param targetLayerSubClassType
     *            The target layers class sub entity (e.g. core layer, than: equivalent to the persistence
     *            class sub entity)
     * @param <P>
     *            Initial layers sub type (e.g. persistence)
     * @param <S>
     *            Initials layers super entity to cast and to map (e.g. Persistence super entity)
     * @param <C>
     *            Target layer sub type (e.g. core layer, mapping-equivalent to persistence sub type)
     *
     * @return A mapped target layer sub entity of a given initial layer superClassEntity. <code>NULL</code>,
     *         if there is no 'super <-> sub'
     */
    protected <P, S, C> C mapSuperTypeToOtherLayersSubType(S initialLayerSuperClassEntity,
        Class<P> initialLayerSubClassType, Class<C> targetLayerSubClassType) {

        LOG.debug("Try to cast superEntity '" + initialLayerSuperClassEntity.getClass().getName()
            + "' to the sub type '" + initialLayerSubClassType.getName() + "'.");

        /*
         * Initial layer: Check, whether the super class entity's type is the one of a sub class
         */
        if (initialLayerSubClassType.isInstance(initialLayerSuperClassEntity)) {

            /*
             * True: 'super <-> sub' relationship exists.
             *
             * Initial Layer [next step]: Cast the super entity to the sub type
             */
            P castedEntity = initialLayerSubClassType.cast(initialLayerSuperClassEntity);

            LOG.debug("Sucessfully casted '" + initialLayerSuperClassEntity.getClass().getName() + "' to '"
                + initialLayerSubClassType.getName() + "'.");

            /*
             * Initial layer -> Target layer: Map the sub entities.
             */
            C subClassCoreEntity = this.mapInitialToTargetLayerEntity(castedEntity, targetLayerSubClassType);

            /*
             * Return the mapped entity of type "target entity sub type".
             */
            return subClassCoreEntity;
        }

        LOG.debug("There is no relationship between the super entity '"
            + initialLayerSuperClassEntity.getClass().getName() + "' and the sub entity '"
            + initialLayerSubClassType.getName() + "'.");

        return null;
    }
}
