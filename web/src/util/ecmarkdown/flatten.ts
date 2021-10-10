import type {
  UnorderedListItemNode as EMDUnorderedListItemNode,
  OrderedListItemNode as EMDOrderedListItemNode,
  ListNode,
  TextNode,
  FormatNode,
  CommentNode,
  TagNode,
  OpaqueTagNode,
} from 'ecmarkdown';
// "EMD": shorthand for ecmarkdown

type WithAlgoMetadata<T> = T & {
  // index of list item. starts from 1
  step: number;

  // indent size. how nested is this list item?
  indent: number
};

type UnorderedListItemNode = WithAlgoMetadata<EMDUnorderedListItemNode>;
type OrderedListItemNode = WithAlgoMetadata<EMDOrderedListItemNode>;
type ListItemNode = UnorderedListItemNode | OrderedListItemNode;
type FragmentNode = TextNode | FormatNode | CommentNode | TagNode | OpaqueTagNode;
export type AlgoStepNode = {
  name: "ordered-list-item" | "unordered-list-item";
  contents: FragmentNode[];
  step: number;
  indent: number;
};

function flattenListItem ( listItem: ListItemNode ) {
  const { name, contents, indent, sublist, step } = listItem;
  const head = { name, contents, indent, step };

  if ( sublist === null )
    return [ head ];

  const flatSublist = flattenList( sublist, indent + 1 )
  return [ head ].concat( flatSublist );
}

export function flattenList ( list: ListNode, indent = 0 ): AlgoStepNode[] {
  const contents: ListItemNode[] = list.contents.map( ( li, step ) => ( {
    ...li,
    indent,
    step: step + 1,
  } ) );

  return contents.flatMap( flattenListItem );
}
