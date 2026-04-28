import { env } from "@/config/env"
import type { ApiResponseWrapper } from "@/types/api"

type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE"

type RequestConfig = {
  method?: HttpMethod
  query?: Record<string, string | number | boolean | undefined | null>
  body?: unknown
  headers?: Record<string, string>
}

export class ApiError extends Error {
  status: number
  statusText: string
  responseBody: string

  constructor(status: number, statusText: string, responseBody: string) {
    super(`[${status}] ${statusText}: ${responseBody}`)
    this.name = "ApiError"
    this.status = status
    this.statusText = statusText
    this.responseBody = responseBody
  }
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
  const isFormData = config.body instanceof FormData
  const requestBody =
    config.body === undefined
      ? undefined
      : isFormData
        ? (config.body as FormData)
        : JSON.stringify(config.body ?? null)
  const headers = {
    ...(isFormData ? {} : { "Content-Type": "application/json" }),
    ...(config.headers ?? {}),
  }
  const response = await fetch(url, {
    method: config.method ?? "GET",
    credentials: "include",
    headers,
    body: requestBody,
  })

  if (!response.ok) {
    const errorBody = await response.text()
    throw new ApiError(response.status, response.statusText, errorBody)
  }

  if (response.status === 204) {
    return undefined as T
  }

  const payload = (await response.json()) as ApiResponseWrapper<T>
  return payload.data
}
