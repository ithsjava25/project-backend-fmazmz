import { useQuery } from "@tanstack/react-query"
import { Link } from "react-router-dom"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { buttonVariants } from "@/components/ui/button"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"

export const UsersPage = () => {
  const usersQuery = useQuery({
    queryKey: ["admin-users"],
    queryFn: caseManagerApi.adminUsers.list,
  })
  const users = usersQuery.data ?? []

  return (
    <div className="space-y-6">
      <PageHeader
        title="Users & Roles"
        description="Browse user accounts and open a user to review and replace roles."
      />
      <Card>
        <CardHeader className="flex flex-row items-center justify-between border-b border-border">
          <CardTitle>User accounts</CardTitle>
          <Link to="/app/users/new" className={buttonVariants()}>
            Create user
          </Link>
        </CardHeader>
        <CardContent className="pt-5">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Email</TableHead>
                <TableHead>Provider</TableHead>
                <TableHead>Roles</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {users.map((account) => (
                <TableRow key={account.id}>
                  <TableCell className="font-medium">
                    <Link to={`/app/users/${account.id}`} className="hover:underline">
                      {account.userName || "(No username)"}
                    </Link>
                  </TableCell>
                  <TableCell>{account.email}</TableCell>
                  <TableCell>{account.provider}</TableCell>
                  <TableCell>{account.roles.join(", ") || "-"}</TableCell>
                </TableRow>
              ))}
              {users.length === 0 && (
                <TableRow>
                  <TableCell colSpan={4} className="py-10 text-center text-muted-foreground">
                    No users available.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  )
}
