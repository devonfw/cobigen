import { Sort } from './sort';

/** Pagination interface used for correctly implementing pagination. */
export interface Pageable {
  pageSize: number;
  pageNumber: number;
  sort?: Sort[];
}
