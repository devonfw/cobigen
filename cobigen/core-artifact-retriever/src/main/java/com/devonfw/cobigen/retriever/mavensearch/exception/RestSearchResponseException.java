package com.devonfw.cobigen.retriever.mavensearch.exception;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryType;

/** Exception to indicate that the REST search API encountered a problem while accessing the server. */
public class RestSearchResponseException extends CobiGenRuntimeException {

  /** The ID */
  private static final long serialVersionUID = 1L;

  /** The status code which was received by the search response */
  private int statusCode = 0;

  /** Message constant if it was not possible to get a response */
  private static final String IT_WAS_NOT_POSSIBLE_TO = "It was not possible to get a response from";

  /** Continuation of IT_WAS_NOT_POSSIBLE_TO */
  private static final String WITH_THE_URL = "with the URL";

  /** Message constant for an unexpected status code */
  private static final String THE_SEARCH_RETURNED_UNEXPECTED_STATUS_CODE = "The search REST API returned the unexpected status code";

  /**
   * Creates a new {@link RestSearchResponseException} with the given message
   *
   * @param message error message of the exception
   */
  public RestSearchResponseException(String message) {

    super(message);
  }

  /**
   * Creates a new {@link RestSearchResponseException} with the specified message and the causing {@link Throwable}
   *
   * @param searchRepositoryType the repository type which got no response
   * @param targetUrl the URL which was used to get a response
   * @param statusCode the status code which was not expected
   */
  public RestSearchResponseException(MavenSearchRepositoryType searchRepositoryType, String targetUrl, int statusCode) {

    super(IT_WAS_NOT_POSSIBLE_TO + ": " + searchRepositoryType + " " + WITH_THE_URL + ": " + targetUrl + "." + "\n"
        + THE_SEARCH_RETURNED_UNEXPECTED_STATUS_CODE + ": " + statusCode + ".");
    this.statusCode = statusCode;
  }

  /**
   * @return statusCode
   */
  public int getStatusCode() {

    return this.statusCode;
  }

}
