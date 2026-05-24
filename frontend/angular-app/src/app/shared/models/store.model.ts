export interface Store {
  id: number;
  code: string;
  name: string;
  active: boolean;
}

export interface CreateStoreRequest {
  code: string;
  name: string;
}
