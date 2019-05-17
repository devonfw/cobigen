package com.maven.project.sampledatamanagement.dataaccess.api.repo;

import static com.querydsl.core.alias.Alias.$;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.querydsl.jpa.impl.JPAQuery;
import java.util.Iterator;

import com.maven.project.sampledatamanagement.common.api.SampleData;
import com.maven.project.sampledatamanagement.dataaccess.api.SampleDataEntity;
import com.maven.project.sampledatamanagement.logic.api.to.SampleDataSearchCriteriaTo;
import com.devonfw.module.jpa.dataaccess.api.QueryUtil;
import com.devonfw.module.jpa.dataaccess.api.data.DefaultRepository;

/**
 * {@link DefaultRepository} for {@link SampleDataEntity}
  */
public interface SampleDataRepository extends DefaultRepository<SampleDataEntity> {

  /**
   * @param criteria the {@link SampleDataSearchCriteriaTo} with the criteria to search.
   * @return the {@link Page} of the {@link SampleDataEntity} objects that matched the search.
   * If no pageable is set, it will return a unique page with all the objects that matched the search.
   */
  default Page<SampleDataEntity> findByCriteria(SampleDataSearchCriteriaTo criteria) {

    SampleDataEntity alias = newDslAlias();
    JPAQuery<SampleDataEntity> query = newDslQuery(alias);

String name = criteria.getName();
if (name != null && !name.isEmpty()) {
QueryUtil.get().whereString(query, $(alias.getName()), name, criteria.getNameOption());
}String surname = criteria.getSurname();
if (surname != null && !surname.isEmpty()) {
QueryUtil.get().whereString(query, $(alias.getSurname()), surname, criteria.getSurnameOption());
}Integer age = criteria.getAge();
if (age != null) {
query.where($(alias.getAge()).eq(age));
}String mail = criteria.getMail();
if (mail != null && !mail.isEmpty()) {
QueryUtil.get().whereString(query, $(alias.getMail()), mail, criteria.getMailOption());
}    if (criteria.getPageable() == null) {
      criteria.setPageable(PageRequest.of(0, Integer.MAX_VALUE));
    } else {
      addOrderBy(query, alias, criteria.getPageable().getSort());
    }
    
    return QueryUtil.get().findPaginated(criteria.getPageable(), query, true);
  }
  
  /**
   * Add sorting to the given query on the given alias
   * 
   * @param query to add sorting to
   * @param alias to retrieve columns from for sorting
   * @param sort specification of sorting
   */
  public default void addOrderBy(JPAQuery<SampleDataEntity> query, SampleDataEntity alias, Sort sort) {
    if (sort != null && sort.isSorted()) {
      Iterator<Order> it = sort.iterator();
      while (it.hasNext()) {
        Order next = it.next();
        switch(next.getProperty()) {
          case "name":
            if (next.isAscending()) {
                query.orderBy($(alias.getName()).asc());
            } else {
                query.orderBy($(alias.getName()).desc());
            }   
          break;
          case "surname":
            if (next.isAscending()) {
                query.orderBy($(alias.getSurname()).asc());
            } else {
                query.orderBy($(alias.getSurname()).desc());
            }   
          break;
          case "age":
            if (next.isAscending()) {
                query.orderBy($(alias.getAge()).asc());
            } else {
                query.orderBy($(alias.getAge()).desc());
            }   
          break;
          case "mail":
            if (next.isAscending()) {
                query.orderBy($(alias.getMail()).asc());
            } else {
                query.orderBy($(alias.getMail()).desc());
            }   
          break;
          default:
            throw new IllegalArgumentException("Sorted by the unknown property '"+next.getProperty()+"'");
        }
      }
    }
}

}