import { createContext, useContext } from "react"
import type { UserResponse } from "@/types/api"

type AuthContextValue = {
  user: UserResponse
}

const AuthContext = createContext<AuthContextValue | null>(null)

export const AuthProvider = AuthContext.Provider

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider")
  }
  return context
}
