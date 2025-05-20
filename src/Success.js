import React from 'react';
import Lottie from 'lottie-react';
import successAnimation from './animations/success-check.json';
import styles from './styles/Success.module.css';

const Success = () => {
  return (
    <div className={styles.successContainer}>
      <div className={styles.lottieWrapper}>
        <Lottie animationData={successAnimation} loop={false} />
      </div>
      <h2 className={styles.successTitle}>Mật Khẩu Đã Được Cập Nhật!</h2>
      <p className={styles.successMessage}>
        Bạn đã thay đổi mật khẩu thành công. Tài khoản của bạn hiện đã an toàn hơn.
      </p>
      <p className={styles.successMessage}>
        Hãy quay lại ứng dụng để đăng nhập và tiếp tục sử dụng dịch vụ.
      </p>
    </div>
  );
};

export default Success;
