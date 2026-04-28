const getRequiredEnv = (key: string, fallback?: string): string => {
  const rawValue = import.meta.env[key]
  if (typeof rawValue === "string" && rawValue.trim().length > 0) {
    return rawValue.trim()
  }
  if (fallback) {
    return fallback
  }
  throw new Error(`Missing required environment variable: ${key}`)
}

export const env = {
  apiBaseUrl: getRequiredEnv("VITE_API_BASE_URL", "http://localhost:8080"),
  openapiUrl: getRequiredEnv("VITE_OPENAPI_URL", "http://localhost:8080/swagger-ui/index.html"),
  openapiJsonUrl: getRequiredEnv("VITE_OPENAPI_JSON_URL", "http://localhost:8080/v3/api-docs"),
  githubLoginUrl: getRequiredEnv(
    "VITE_GITHUB_LOGIN_URL",
    "http://localhost:8080/oauth2/authorization/github",
  ),
}
