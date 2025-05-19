import { BrowserRouter, Routes, Route } from 'react-router-dom';
import ResetPasswordForm from './ResetPasswordForm.js';
import Success from './Success.js';
import Failure from './Failure.js';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/reset-password/:token" element={<ResetPasswordForm />} />
        <Route path="/success" element={<Success />} />
        <Route path="/failure" element={<Failure />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
