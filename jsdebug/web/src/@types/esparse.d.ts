// esparse type definition
declare class ESParse {
  // constructor
  constructor ( version: string );
  // return stringified, compressed form of parsing result from translator
  parseWithCompress ( code ): string;
}
