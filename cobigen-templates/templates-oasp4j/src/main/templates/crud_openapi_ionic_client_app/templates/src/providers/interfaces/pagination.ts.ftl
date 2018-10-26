/** Pagination interface used for correctly implementing pagination. */
export interface Pagination{
    size: number;
    page: number;
    total: boolean;
}
