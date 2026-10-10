import { Route, Routes } from "react-router-dom";
import { CampaignsPage } from "./pages/CampaignsPage";

function App() {
  return (
    <Routes>
      <Route path="/" element={<CampaignsPage />} />
    </Routes>
  );
}

export default App;
