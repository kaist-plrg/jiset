import React from 'react';
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
  emit ( node: Node | Node[] ) {
    return this.emitNode( node );
  }

  static emit ( node: Node | Node[] ) {
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
    return React.createElement( React.Fragment, null, tag.contents );
  }

  emitText ( text: TextNode ) {
    return React.createElement( React.Fragment, null, text.contents );
  }

  emitTick ( node: TickNode ) {
    return this.wrapFragment( 'code', node.contents );
  }

  emitTilde ( node: TildeNode ) {
    return this.wrapFragment( 'emu-const', node.contents );
  }

  emitFragment ( fragment: Node[] ): JSX.Element {
    return React.createElement( React.Fragment, null, fragment.map( p => this.emitNode( p ) ) );
  }

  emitPipe ( pipe: PipeNode ) {
    const props = {
      optional: pipe.optional,
      params: pipe.params || false,
    };

    return React.createElement( 'emu-nt', props, pipe.nonTerminal );
  }

  wrapFragment ( wrapping: string, fragment: Node[] ) {
    const children = this.emitFragment( fragment );

    // react element with no props and children
    return React.createElement( wrapping, null, children );
  }
}
