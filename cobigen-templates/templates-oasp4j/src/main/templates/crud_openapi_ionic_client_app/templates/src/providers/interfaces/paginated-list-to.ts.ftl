import { Pagination } from "./pagination";

/** Generic paginated transfer object where you can set the entity type. */
export interface PaginatedListTo<T> {
    pagination: Pagination,
    result: Array<T>,
}