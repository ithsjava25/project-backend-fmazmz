import { env } from "@/config/env"
import type { ApiResponseWrapper } from "@/types/api"

type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE"

type RequestConfig = {
  method?: HttpMethod
  query?: Record<string, string | number | boolean | undefined | null>
  body?: unknown
  headers?: Record<string, string>
}

const withQuery = (path: string, query?: RequestConfig["query"]) => {
  if (!query) {
    return path
  }
  const params = new URLSearchParams()
  Object.entries(query).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      params.set(key, String(value))
    }
  })
  const asString = params.toString()
  return asString.length > 0 ? `${path}?${asString}` : path
}

export const request = async <T>(path: string, config: RequestConfig = {}): Promise<T> => {
  const url = `${env.apiBaseUrl}${withQuery(path, config.query)}`
  const response = await fetch(url, {
    method: config.method ?? "GET",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...(config.headers ?? {}),
    },
    body: config.body === undefined ? undefined : JSON.stringify(config.body),
  })

  if (!response.ok) {
    const errorBody = await response.text()
    throw new Error(`[${response.status}] ${response.statusText}: ${errorBody}`)
  }

  if (response.status === 204) {
    return undefined as T
  }

  const payload = (await response.json()) as ApiResponseWrapper<T>
  return payload.data
}
