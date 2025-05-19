import React from 'react';
import styles from './styles/Success.module.css';

const Success = () => {
    return (
        <div className={styles.successContainer}>
            <h2 className={styles.successTitle}>Đổi Mật Khẩu Thành Công!</h2>
            <p className={styles.successMessage}>Mật khẩu của bạn đã được cập nhật thành công.</p>
        </div>
    );
};

export default Success;