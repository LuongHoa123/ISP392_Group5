/**
 * Validation cho form quản lý lịch làm việc
 */
class ShiftFormValidator {
    constructor(formId) {
        this.form = document.getElementById(formId);
        this.fixedTimeSelect = document.getElementById('fixedTime');
        this.roomSelect = document.getElementById('roomSelect');
        this.doctorSelect = document.getElementById('doctorSelect');
        this.nurseSelect = document.getElementById('nurseSelect');
        
        this.initValidation();
    }

    initValidation() {
        // Thêm CSS cho validation
        this.addValidationStyles();
        
        // Bind events
        this.bindEvents();
    }

    addValidationStyles() {
        const style = document.createElement('style');
        style.textContent = `
            .form-control.is-invalid,
            .form-select.is-invalid {
                border-color: #dc3545;
                box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
            }
            
            .invalid-feedback {
                display: block;
                color: #dc3545;
                font-size: 0.875em;
                margin-top: 0.25rem;
            }
            
            .form-label.required::after {
                content: " *";
                color: #dc3545;
            }
        `;
        document.head.appendChild(style);
    }

    bindEvents() {
        // Real-time validation
        this.fixedTimeSelect?.addEventListener('change', () => this.validateField(this.fixedTimeSelect, 'Vui lòng chọn khung giờ'));
        this.roomSelect?.addEventListener('change', () => this.validateField(this.roomSelect, 'Vui lòng chọn phòng'));
        this.doctorSelect?.addEventListener('change', () => this.validateField(this.doctorSelect, 'Vui lòng chọn bác sĩ'));
        
        // Form submission
        this.form?.addEventListener('submit', (e) => this.handleSubmit(e));
    }

    validateField(field, errorMessage) {
        if (!field) return true;
        
        const isValid = field.value && field.value.trim() !== '';
        
        if (isValid) {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
        } else {
            field.classList.remove('is-valid');
            field.classList.add('is-invalid');
        }
        
        return isValid;
    }

    validateDoctorRoomCompatibility() {
        if (!this.roomSelect?.value || !this.doctorSelect?.value) {
            return true;
        }

        const roomType = this.roomSelect.options[this.roomSelect.selectedIndex].text;
        const doctorText = this.doctorSelect.options[this.doctorSelect.selectedIndex].text;
        
        let isValid = true;
        let errorMessage = '';

        if (roomType.includes('Phòng tai') && !doctorText.includes('(Tai)')) {
            errorMessage = 'Bác sĩ được chọn không phù hợp với phòng Tai. Vui lòng chọn bác sĩ chuyên khoa Tai.';
            isValid = false;
        } else if (roomType.includes('Phòng mũi') && !doctorText.includes('(Mũi)')) {
            errorMessage = 'Bác sĩ được chọn không phù hợp với phòng Mũi. Vui lòng chọn bác sĩ chuyên khoa Mũi.';
            isValid = false;
        } else if (roomType.includes('Phòng họng') && !doctorText.includes('(Họng)')) {
            errorMessage = 'Bác sĩ được chọn không phù hợp với phòng Họng. Vui lòng chọn bác sĩ chuyên khoa Họng.';
            isValid = false;
        } else if ((roomType.includes('nội soi') || roomType.includes('thủ thuật')) && 
                   !doctorText.includes('(Tai)') && !doctorText.includes('(Mũi)') && !doctorText.includes('(Họng)')) {
            errorMessage = 'Bác sĩ được chọn không phù hợp với phòng này. Vui lòng chọn bác sĩ chuyên khoa Tai, Mũi hoặc Họng.';
            isValid = false;
        }

        if (!isValid) {
            this.doctorSelect.classList.add('is-invalid');
            const errorElement = document.getElementById('doctorError');
            if (errorElement) {
                errorElement.textContent = errorMessage;
            }
        } else {
            this.doctorSelect.classList.remove('is-invalid');
        }

        return isValid;
    }

    validateForm() {
        let isValid = true;
        
        // Validate các trường bắt buộc
        isValid = this.validateField(this.fixedTimeSelect, 'Vui lòng chọn khung giờ') && isValid;
        isValid = this.validateField(this.roomSelect, 'Vui lòng chọn phòng') && isValid;
        isValid = this.validateField(this.doctorSelect, 'Vui lòng chọn bác sĩ') && isValid;
        
        // Validate tính tương thích giữa bác sĩ và phòng
        isValid = this.validateDoctorRoomCompatibility() && isValid;
        
        return isValid;
    }

    clearValidationErrors() {
        const inputs = this.form?.querySelectorAll('.form-select, .form-control');
        inputs?.forEach(input => {
            input.classList.remove('is-invalid', 'is-valid');
        });
    }

    handleSubmit(e) {
        e.preventDefault();
        
        if (!this.validateForm()) {
            alert('Vui lòng điền đầy đủ thông tin bắt buộc và đảm bảo bác sĩ phù hợp với phòng được chọn.');
            return false;
        }
        
        return true;
    }

    // Phương thức để reset form
    resetForm() {
        this.form?.reset();
        this.clearValidationErrors();
    }
}

// Export để sử dụng trong các file khác
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ShiftFormValidator;
} 