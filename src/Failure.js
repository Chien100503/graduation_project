import React from 'react';
import Lottie from 'lottie-react';
import failureAnimation from './animations/failure-cross.json';
import styles from './styles/Failure.module.css';

const Failure = ({ message }) => {
  return (
    <div className={styles.failureContainer}>
      <div className={styles.lottieWrapper}>
        <Lottie animationData={failureAnimation} loop={false} />
      </div>
      <h2 className={styles.failureTitle}>Đổi Mật Khẩu Thất Bại!</h2>
      <p className={styles.failureMessage}>
        {message || 'Có lỗi xảy ra khi cập nhật mật khẩu. Vui lòng kiểm tra lại thông tin và thử lại.'}
      </p>
      <button className={styles.retryButton} onClick={() => window.location.reload()}>
        Thử lại
      </button>
    </div>
  );
};

export default Failure;
