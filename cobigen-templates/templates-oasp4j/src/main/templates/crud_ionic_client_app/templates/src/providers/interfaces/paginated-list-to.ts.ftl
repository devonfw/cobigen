import { Pageable } from './pageable';

/** Generic paginated transfer object where you can set the entity type. */
export interface PaginatedListTo<T> {
  pageable: Pageable;
  content: Array<T>;
}
