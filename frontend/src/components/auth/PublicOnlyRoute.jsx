import { Navigate, Outlet } from "react-router";

import { useAuth } from "../../context/AuthContext";

function PublicOnlyRoute() {
  const { isAuthenticated } = useAuth();

  if (isAuthenticated) {
    return <Navigate to="/app" replace />;
  }

  return <Outlet />;
}

export default PublicOnlyRoute;
