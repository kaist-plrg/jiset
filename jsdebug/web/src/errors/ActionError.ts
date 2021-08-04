// error class for controller
class ActionError extends Error {
  constructor ( ...params: any[] ) {
    super( ...params );
    if ( Error.captureStackTrace ) {
      Error.captureStackTrace( this, ActionError );
    }
  }
}

export default ActionError;
