import React from 'react';
import styles from './styles/Failure.module.css';

const Failure = ({ message }) => {
    return (
        <div className={styles.failureContainer}>
            <h2 className={styles.failureTitle}>Đổi Mật Khẩu Thất Bại!</h2>
            <p className={styles.failureMessage}>{message || 'Đã xảy ra lỗi khi đổi mật khẩu. Vui lòng thử lại.'}</p>
        </div>
    );
};

export default Failure;