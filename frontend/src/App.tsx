import { Route, Routes } from "react-router-dom";
import { AppHeader } from "./components/AppHeader";
import { CampaignsPage } from "./pages/CampaignsPage";
import { LoginPage } from "./pages/LoginPage";
import { RegisterPage } from "./pages/RegisterPage";

function App() {
  return (
    <div className="app">
      <AppHeader />
      <Routes>
        <Route path="/" element={<CampaignsPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Routes>
    </div>
  );
}

export default App;
