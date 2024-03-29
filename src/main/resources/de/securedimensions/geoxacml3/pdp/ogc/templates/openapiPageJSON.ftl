{
  "openapi": "3.0.2",
  "info": {
    "title": "OGC GeoXACML 3.0 - OpenAPI 3.0",
    "description": "This is the implementation based on the OGC GeoXACML 3.0 specification.",
    "contact": {
      "email": "info@secure-dimensions.de"
    },
    "license": {
      "name": "Apache 2.0",
      "url": "http://www.apache.org/licenses/LICENSE-2.0.html"
    },
    "version": "0.1"
  },
  "externalDocs": {
    "description": "Find out more about GeoXACML 3.0",
    "url": "https://www.ogc.org/projects/groups/geoxacmlswg"
  },
  "servers": [
    {
      "url": "${PDP_URL}"
    }
  ],
  "tags": [
    {
      "name": "geoPDP",
      "description": "GeoXACML 3.0 compliant endpoint to receive Authorization Decisions"
    }
  ],
  "paths": {
    "/api": {
      "get": {
      "tags": [
        "geoPDP"
      ],
      "summary": "OpenAPI",
      "description": "The OpenAPI description",
      "operationId": "getAPI",
      "parameters": [
        {
          "name": "f",
          "in": "query",
          "description": "Format of the page",
          "required": false,
          "explode": true,
          "schema": {
            "type": "string",
            "default": "html",
            "enum": [
              "html",
              "json"
            ]
          }
        }
      ],
      "responses": {
        "200": {
          "description": "OpenAPI representation",
          "content": {
            "text/html": {
              "schema": {
                "type": "object"
              }
            },
            "application/json": {
              "schema": {
                "type": "object"
              }
            }
          }
        }
      }
    }
    },
    "/conformance": {
      "get": {
        "tags": [
          "geoPDP"
        ],
        "summary": "OGC Conformance",
        "description": "The conformance page",
        "operationId": "getConformance",
        "parameters": [
          {
            "name": "f",
            "in": "query",
            "description": "Format of the page",
            "required": false,
            "explode": true,
            "schema": {
              "type": "string",
              "default": "json",
              "enum": [
                "html",
                "json"
              ]
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OpenAPI representation",
            "content": {
              "text/html": {
                "schema": {
                  "type": "object"
                }
              },
              "application/json": {
                "schema": {
                  "type": "object"
                }
              }
            }
          }
        }
      }
    },
    "/decision": {
      "post": {
        "tags": [
          "geoPDP"
        ],
        "summary": "Request an Authorization Decision",
        "description": "Use GeoXACML 3.0 or XACML 3.0 request structure",
        "operationId": "getAD",
        "requestBody": {
          "description": "GeoXACML 3.0 ADR",
          "content": {
            "application/geoxacml+json": {
              "schema": {
                "$ref": "#/components/schemas/Request"
              }
            },
            "application/xacml+json": {
              "schema": {
                "$ref": "#/components/schemas/Request"
              }
            },
            "application/geoxacml+xml": {
              "schema": {
                "$ref": "#/components/schemas/Request"
              }
            },
            "application/xacml+xml": {
              "schema": {
                "$ref": "#/components/schemas/Request"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "Decision",
            "content": {
              "application/geoxacml+json": {
                "schema": {
                  "$ref": "#/components/schemas/Response"
                }
              },
              "application/geoxacml+xml": {
                "schema": {
                  "$ref": "#/components/schemas/Response"
                }
              }
            }
          },
          "415": {
            "description": "Media type not supported"
          }
        }
      }
    }
  },
  "components": {
    "schemas": {
      "Request": {
        "title": "GeoXACML ADR",
        "description": "GeoXACML structure compliant Authorization Decision Request",
        "type": "object"
      },
      "Response": {
        "title": "GeoXACML AD",
        "description": "XACML structure compliant Authorization Decision",
        "type": "object"
      }
    }
  }
}