import { DataSource } from '@angular/cdk/collections';
import { MatPaginator } from '@angular/material/paginator';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { switchMap, map, catchError, startWith } from 'rxjs/operators';
import { Product } from '../models/product.model';
import { ProductService } from '../services/product.service';

export class ProductDataSource extends DataSource<Product> {
  private dataSubject = new BehaviorSubject<Product[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private totalElementsSubject = new BehaviorSubject<number>(0);

  public loading$ = this.loadingSubject.asObservable();
  public totalElements$ = this.totalElementsSubject.asObservable();

  constructor(
    private productService: ProductService,
    private paginator: MatPaginator,
  ) {
    super();
  }

  /**
   * The connect method is called by the table to receive a stream that emits the data array
   * that should be rendered. This is the key method for server-side pagination.
   */
  connect(): Observable<Product[]> {
    // Listen to paginator page changes
    const paginatorChanges = this.paginator.page.pipe(
      startWith({ pageIndex: 0, pageSize: 10 }),
    );

    return paginatorChanges.pipe(
      switchMap((event) => {
        this.loadingSubject.next(true);

        // Call the service with current pagination parameters
        return this.productService
          .getProducts({
            page: event.pageIndex,
            size: event.pageSize
          })
          .pipe(
            catchError((error) => {
              console.error('Error loading products:', error);
              return of({
                content: [],
                totalElements: 0,
                totalPages: 0,
                size: 0,
                number: 0,
              });
            }),
          );
      }),
      map((response) => {
        this.loadingSubject.next(false);
        this.totalElementsSubject.next(response.totalElements);

        // Update paginator length with total count from server
        this.paginator.length = response.totalElements;

        return response.content;
      }),
    );
  }

  disconnect(): void {
    this.dataSubject.complete();
    this.loadingSubject.complete();
    this.totalElementsSubject.complete();
  }

  /**
   * Manual method to trigger data loading (useful for refresh operations)
   */
  loadData(pageIndex = 0, pageSize = 10): void {
    this.loadingSubject.next(true);

    this.productService
      .getProducts({
        page: pageIndex,
        size: pageSize
      })
      .pipe(
        catchError((error) => {
          console.error('Error loading products:', error);
          return of({
            content: [],
            totalElements: 0,
            totalPages: 0,
            size: 0,
            number: 0,
          });
        }),
      )
      .subscribe((response) => {
        this.dataSubject.next(response.content);
        this.totalElementsSubject.next(response.totalElements);
        this.loadingSubject.next(false);

        // Update paginator
        this.paginator.length = response.totalElements;
        this.paginator.pageIndex = pageIndex;
        this.paginator.pageSize = pageSize;
      });
  }
}
