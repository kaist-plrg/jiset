const API_HOST = "http://localhost:8080";

// json header
export const mkJSONHeader = (): Record<string, string> => {
  const headers: { [index: string]: string } = {};
  headers["Content-Type"] = "application/json";
  headers["Accept"] = "application/json";
  return headers;
};

// trim slash
export const trim_slash = (input: string): string => {
  return input.replace(/\/+$/, "").replace(/^\/+/, "");
};

// make url for GET request
export const mkURL = (
  host: string,
  endpoint: string,
  queryObj: { [key: string]: unknown } = {}
): string => {
  let url = `${trim_slash(host)}/${trim_slash(endpoint)}`;
  const listParams: string[] = [];
  for (const key in queryObj) {
    const entry = queryObj[key];
    if (
      typeof entry === "string" ||
      typeof entry === "number" ||
      typeof entry === "boolean"
    ) {
      const param = `${encodeURIComponent(key)}=${encodeURIComponent(
        entry.toString()
      )}`;
      listParams.push(param);
    } else if (entry !== undefined || entry !== null) {
      throw new Error(`Not supported entry type: ${typeof entry}(${entry})`);
    }
  }
  if (listParams.length > 0) {
    const querystring = listParams.join("&");
    url += `?${querystring}`;
  }
  return url;
};

// HTTP methods
type HTTPMethod =
  | "DELETE"
  | "GET"
  | "HEAD"
  | "PATCH"
  | "POST"
  | "PUT"
  | "OPTIONS";

// raw GET request
const doGetRequest = async (
  host: string,
  endpoint: string,
  queryObj?: { [key: string]: any }
): Promise<unknown> => {
  try {
    const url = mkURL(host, endpoint, queryObj);
    const response = await fetch(url, { method: "GET" });
    if (!response.ok)
      throw `GET request to ${url} failed with ${response.status}`;
    return await response.json();
  } catch (e) {
    throw new Error(e);
  }
};

// raw POST-like request
const doWriteRequest = async (
  host: string,
  method: HTTPMethod,
  endpoint: string,
  bodyObj?: unknown
): Promise<unknown> => {
  try {
    const url = mkURL(host, endpoint);
    const response = await fetch(url, {
      method,
      headers: {
        ...(bodyObj ? mkJSONHeader() : undefined),
      },
      body: bodyObj ? JSON.stringify(bodyObj) : undefined,
    });
    if (!response.ok)
      throw `${method} request to ${url} failed with ${response.status}`;
    return await response.json();
  } catch (e) {
    throw new Error(e);
  }
};

// GET request to API server
export const doAPIGetRequest = (
  endpoint: string,
  queryObj?: { [key: string]: any }
): Promise<unknown> => {
  return doGetRequest(API_HOST, endpoint, queryObj);
};

// POST-like request to API server
const doAPIWriteRequest = (
  method: HTTPMethod,
  endpoint: string,
  bodyObj?: unknown
): Promise<unknown> => {
  return doWriteRequest(API_HOST, method, endpoint, bodyObj);
};
// POST
export const doAPIPostRequest = (
  endpoint: string,
  bodyObj?: unknown
): Promise<unknown> => {
  return doAPIWriteRequest("POST", endpoint, bodyObj);
};
// DELETE
export const doAPIDeleteRequest = (
  endpoint: string,
  bodyObj?: unknown
): Promise<unknown> => {
  return doAPIWriteRequest("DELETE", endpoint, bodyObj);
};
// PUT
export const doAPIPutRequest = (
  endpoint: string,
  bodyObj?: unknown
): Promise<unknown> => {
  return doAPIWriteRequest("PUT", endpoint, bodyObj);
};
