package ${variables.rootPackage}.general.common.api.to;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.data.domain.Pageable;

/**
 * This class will be used if SOAP service needs to return PaginatedList result.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PaginatedListToWrapper<T> {

  private Pageable pagination;

  @XmlElement
  private List<T> result;

  /**
   * @return pagination
   */
  public Pageable getPagination() {

    return this.pagination;
  }

  /**
   * @return result
   */
  public List<T> getResult() {

    return this.result;
  }

  /**
   * @param result new value of {@link #getresult}.
   */
  public void setResult(List<T> result) {

    this.result = result;
  }

  /**
   * @param pagination new value of {@link #getpagination}.
   */
  public void setPagination(Pageable pagination) {

    this.pagination = pagination;
  }

}
