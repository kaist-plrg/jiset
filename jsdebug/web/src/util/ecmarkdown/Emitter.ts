import { Fragment, createElement as e } from 'react';
import { v4 as uuid } from "uuid";
import type {
  Node,
  TextNode,
  CommentNode,
  TagNode,
  OpaqueTagNode,
  StarNode,
  UnderscoreNode,
  TickNode,
  TildeNode,
  PipeNode
} from 'ecmarkdown';

export class Emitter {
  emit ( node: Node[] ) {
    return this.emitNode( node );
  }

  static emit ( node: Node[] ) {
    const emitter = new Emitter();
    return emitter.emit( node );
  }

  emitNode ( node: Node | Node[] ) {
    if ( Array.isArray( node ) ) return this.emitFragment( node );

    switch ( node.name ) {
      case 'text':
        return this.emitText( node );
      case 'pipe':
        return this.emitPipe( node );
      case 'star':
        return this.emitStar( node );
      case 'underscore':
        return this.emitUnderscore( node );
      case 'tick':
        return this.emitTick( node );
      case 'tilde':
        return this.emitTilde( node );
      case 'comment':
      case 'tag':
      case 'opaqueTag':
        return this.emitTag( node );
      default:
        // @ts-ignore
        throw new Error( "Can't emit " + node.name );
    }
  }

  emitStar ( node: StarNode ) {
    return this.wrapFragment( 'emu-val', node.contents );
  }

  emitUnderscore ( node: UnderscoreNode ) {
    return this.wrapFragment( 'var', node.contents );
  }

  emitTag ( tag: OpaqueTagNode | CommentNode | TagNode ) {
    if (tag.name === 'tag') {
      if (tag.contents.startsWith("<emu-xref")) {
        const specURL = "https://tc39.es/ecma262/";
        const regex = /href="#([a-zA-Z-]*)"/;

        // first element is the first complete match,
        // the following elements are capturing groups
        const matches = tag.contents.match(regex);
        const href = matches?.[1];

        // capturing group exists
        if (href) {
          // NOTE: href does NOT contain "#"
          const anchorProps = { href: `${specURL}#${href}`, target: '_blank' };
          const anchor = e('a', anchorProps, href + ' ');
          return e('emu-xref', { key: uuid() }, anchor);
        }
      } else if (tag.contents === "</emu-xref>")
        return null;
    }

    return e( Fragment, { key: uuid() }, tag.contents );
  }

  emitText ( text: TextNode ) {
    return e( Fragment, { key: uuid() }, text.contents );
  }

  emitTick ( node: TickNode ) {
    return this.wrapFragment( 'code', node.contents );
  }

  emitTilde ( node: TildeNode ) {
    return this.wrapFragment( 'emu-const', node.contents );
  }

  emitFragment ( fragment: Node[] ): JSX.Element {
    return e( Fragment, null, fragment.map( p => this.emitNode( p ) ) );
  }

  emitPipe ( pipe: PipeNode ) {
    const props = {
      optional: pipe.optional,
      params: pipe.params || false,
      key: uuid(),
    };

    return e( 'emu-nt', props, pipe.nonTerminal );
  }

  wrapFragment ( wrapping: string, fragment: Node[] ) {
    const children = this.emitFragment( fragment );

    // react element with no props and children
    return e( wrapping, { key: uuid() }, children );
  }
}
