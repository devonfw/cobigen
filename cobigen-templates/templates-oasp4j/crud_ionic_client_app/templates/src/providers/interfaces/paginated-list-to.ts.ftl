import { Pagination } from "./pagination";

export interface PaginatedListTo<T> {
    pagination: Pagination,
    result: Array<T>,
}