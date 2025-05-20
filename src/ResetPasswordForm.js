import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import styles from "./styles/ResetPasswordForm.module.css";
import api from "./config/config";
import ChangePasswordSuccess from "./Success";
import ChangePasswordFailure from "./Failure";
import { FaEye, FaEyeSlash } from "react-icons/fa";

const ResetPasswordForm = () => {
  const { token } = useParams();
  const navigate = useNavigate();
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [newPasswordError, setNewPasswordError] = useState("");
  const [confirmPasswordError, setConfirmPasswordError] = useState("");
  const [error, setError] = useState("");
  const [status, setStatus] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError("");
    setNewPasswordError("");
    setConfirmPasswordError("");
    setStatus(null);

    let isValid = true;

    if (newPassword.length < 8) {
      setNewPasswordError("Mật khẩu phải có ít nhất 8 ký tự.");
      isValid = false;
    }

    if (newPassword !== confirmPassword) {
      setConfirmPasswordError("Mật khẩu không khớp.");
      isValid = false;
    }

    if (isValid) {
      try {
        const responseData = await api.post(`/reset-password/${token}`, {
          password: newPassword,
          confirmPassword: confirmPassword,
        });

        if (responseData && responseData.success === true) {
          setStatus("success");
        } else {
          setError(responseData?.message || "Đã xảy ra lỗi khi đặt lại mật khẩu.");
          setStatus("failure");
        }
      } catch (err) {
        console.error("API Error:", err);
        setError(err.message || "Lỗi kết nối đến server.");
        setStatus("failure");
      } finally {
        setIsLoading(false);
      }
    } else {
      setIsLoading(false);
    }
  };

  if (status === "success") {
    return <ChangePasswordSuccess onGoToLogin={() => navigate("/success")} />;
  }

  if (status === "failure") {
    return <ChangePasswordFailure message={error} />;
  }

  return (
    <div className={styles.resetPasswordContainer}>
      <div className={styles.resetPasswordFormWrapper}>
        <h2 className={styles.resetPasswordTitle}>Đặt Lại Mật Khẩu</h2>

        {error && status === "failure" && <div className={styles.error}>{error}</div>}

        <form onSubmit={handleSubmit} className={styles.resetPasswordForm}>
          <div className={styles.formGroup}>
            <label htmlFor="newPassword" className={styles.label}>
              Mật Khẩu Mới:
            </label>
            <div className={styles.passwordInputWrapper}>
              <input
                type={showNewPassword ? "text" : "password"}
                id="newPassword"
                placeholder="New password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                className={`${styles.input} ${newPasswordError ? styles.invalid : ""}`}
                disabled={isLoading}
              />
              <span className={styles.eyeIcon} onClick={() => setShowNewPassword((prev) => !prev)}>
                {showNewPassword ? <FaEyeSlash /> : <FaEye />}
              </span>
            </div>
            {newPasswordError && <p className={styles.errorMessage}>{newPasswordError}</p>}
          </div>

          <div className={styles.formGroup}>
            <label htmlFor="confirmPassword" className={styles.label}>
              Xác Nhận Mật Khẩu:
            </label>
            <div className={styles.passwordInputWrapper}>
              <input
                type={showConfirmPassword ? "text" : "password"}
                id="confirmPassword"
                placeholder="Confirm Password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className={`${styles.input} ${confirmPasswordError ? styles.invalid : ""}`}
                disabled={isLoading}
              />
              <span className={styles.eyeIcon} onClick={() => setShowConfirmPassword((prev) => !prev)}>
                {showConfirmPassword ? <FaEyeSlash /> : <FaEye />}
              </span>
            </div>
            {confirmPasswordError && <p className={styles.errorMessage}>{confirmPasswordError}</p>}
          </div>

          <button type="submit" className={styles.submitButton} disabled={isLoading}>
            {isLoading ? "Đang xử lý..." : "Đặt Lại Mật Khẩu"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default ResetPasswordForm;
