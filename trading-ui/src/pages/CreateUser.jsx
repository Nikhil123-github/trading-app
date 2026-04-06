import { useState } from "react";

const styles = `
  @import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Mono:wght@300;400;500&display=swap');

  * { box-sizing: border-box; margin: 0; padding: 0; }

  .cu-root {
    min-height: 100vh;
    background: #0a0a0f;
    font-family: 'DM Mono', monospace;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    position: relative;
  }

  .cu-bg-grid {
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
    background-size: 48px 48px;
    pointer-events: none;
  }

  .cu-bg-glow {
    position: absolute;
    width: 600px;
    height: 600px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(0,255,163,0.06) 0%, transparent 70%);
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    pointer-events: none;
  }

  .cu-card {
    position: relative;
    width: 480px;
    background: #111118;
    border: 1px solid rgba(255,255,255,0.08);
    border-radius: 2px;
    padding: 48px;
    z-index: 1;
  }

  .cu-card::before {
    content: '';
    position: absolute;
    top: 0; left: 0; right: 0;
    height: 2px;
    background: linear-gradient(90deg, transparent, #00ffa3, transparent);
  }

  .cu-ticker {
    font-family: 'DM Mono', monospace;
    font-size: 10px;
    font-weight: 500;
    letter-spacing: 3px;
    color: #00ffa3;
    text-transform: uppercase;
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .cu-ticker::before {
    content: '';
    display: inline-block;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #00ffa3;
    animation: pulse 2s infinite;
  }

  @keyframes pulse {
    0%, 100% { opacity: 1; transform: scale(1); }
    50% { opacity: 0.4; transform: scale(0.8); }
  }

  .cu-title {
    font-family: 'Syne', sans-serif;
    font-size: 36px;
    font-weight: 800;
    color: #ffffff;
    line-height: 1.1;
    margin-bottom: 8px;
    letter-spacing: -1px;
  }

  .cu-title span {
    color: #00ffa3;
  }

  .cu-subtitle {
    font-size: 12px;
    color: rgba(255,255,255,0.3);
    margin-bottom: 40px;
    letter-spacing: 0.5px;
  }

  .cu-field {
    margin-bottom: 24px;
  }

  .cu-label {
    display: block;
    font-size: 10px;
    letter-spacing: 2px;
    color: rgba(255,255,255,0.4);
    text-transform: uppercase;
    margin-bottom: 8px;
  }

  .cu-input-wrap {
    position: relative;
  }

  .cu-input {
    width: 100%;
    background: rgba(255,255,255,0.03);
    border: 1px solid rgba(255,255,255,0.08);
    border-radius: 2px;
    padding: 14px 16px;
    font-family: 'DM Mono', monospace;
    font-size: 14px;
    color: #ffffff;
    outline: none;
    transition: border-color 0.2s, background 0.2s;
  }

  .cu-input::placeholder {
    color: rgba(255,255,255,0.15);
  }

  .cu-input:focus {
    border-color: #00ffa3;
    background: rgba(0,255,163,0.03);
  }

  .cu-input.error {
    border-color: #ff4f4f;
  }

  .cu-error {
    font-size: 11px;
    color: #ff4f4f;
    margin-top: 6px;
    letter-spacing: 0.5px;
  }

  .cu-balance-info {
    background: rgba(0,255,163,0.04);
    border: 1px solid rgba(0,255,163,0.12);
    border-radius: 2px;
    padding: 14px 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;
  }

  .cu-balance-label {
    font-size: 10px;
    letter-spacing: 2px;
    color: rgba(255,255,255,0.3);
    text-transform: uppercase;
  }

  .cu-balance-value {
    font-family: 'Syne', sans-serif;
    font-size: 18px;
    font-weight: 700;
    color: #00ffa3;
  }

  .cu-btn {
    width: 100%;
    padding: 16px;
    background: #00ffa3;
    border: none;
    border-radius: 2px;
    font-family: 'Syne', sans-serif;
    font-size: 14px;
    font-weight: 700;
    letter-spacing: 2px;
    text-transform: uppercase;
    color: #0a0a0f;
    cursor: pointer;
    transition: all 0.2s;
    position: relative;
    overflow: hidden;
  }

  .cu-btn:hover:not(:disabled) {
    background: #00e691;
    transform: translateY(-1px);
  }

  .cu-btn:active:not(:disabled) {
    transform: translateY(0);
  }

  .cu-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .cu-btn-loading {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
  }

  .cu-spinner {
    width: 14px;
    height: 14px;
    border: 2px solid rgba(10,10,15,0.3);
    border-top-color: #0a0a0f;
    border-radius: 50%;
    animation: spin 0.6s linear infinite;
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }

  /* Success state */
  .cu-success {
    text-align: center;
    padding: 16px 0;
    animation: fadeIn 0.4s ease;
  }

  @keyframes fadeIn {
    from { opacity: 0; transform: translateY(8px); }
    to { opacity: 1; transform: translateY(0); }
  }

  .cu-success-icon {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    border: 2px solid #00ffa3;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;
    font-size: 22px;
  }

  .cu-success-title {
    font-family: 'Syne', sans-serif;
    font-size: 22px;
    font-weight: 800;
    color: #ffffff;
    margin-bottom: 8px;
  }

  .cu-success-msg {
    font-size: 12px;
    color: rgba(255,255,255,0.3);
    margin-bottom: 24px;
  }

  .cu-user-card {
    background: rgba(0,255,163,0.04);
    border: 1px solid rgba(0,255,163,0.12);
    border-radius: 2px;
    padding: 20px;
    text-align: left;
    margin-bottom: 24px;
  }

  .cu-user-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6px 0;
    border-bottom: 1px solid rgba(255,255,255,0.04);
  }

  .cu-user-row:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }

  .cu-user-key {
    font-size: 10px;
    letter-spacing: 1.5px;
    color: rgba(255,255,255,0.3);
    text-transform: uppercase;
  }

  .cu-user-val {
    font-size: 13px;
    color: #ffffff;
  }

  .cu-user-val.green {
    color: #00ffa3;
  }

  .cu-reset-btn {
    width: 100%;
    padding: 14px;
    background: transparent;
    border: 1px solid rgba(255,255,255,0.08);
    border-radius: 2px;
    font-family: 'DM Mono', monospace;
    font-size: 12px;
    letter-spacing: 1px;
    color: rgba(255,255,255,0.4);
    cursor: pointer;
    transition: all 0.2s;
  }

  .cu-reset-btn:hover {
    border-color: rgba(255,255,255,0.2);
    color: rgba(255,255,255,0.7);
  }
`;

export default function CreateUser() {
  const [form, setForm] = useState({ name: "", email: "" });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [created, setCreated] = useState(null);
  const [apiError, setApiError] = useState("");

  const validate = () => {
    const e = {};
    if (!form.name.trim()) e.name = "// name is required";
    if (!form.email.trim()) e.email = "// email is required";
    else if (!/\S+@\S+\.\S+/.test(form.email)) e.email = "// invalid email format";
    return e;
  };

  const handleSubmit = async () => {
    const e = validate();
    if (Object.keys(e).length) { setErrors(e); return; }
    setErrors({});
    setApiError("");
    setLoading(true);
    try {
      const res = await fetch("http://localhost:8081/user/add", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: form.name, email: form.email }),
      });
      if (!res.ok) throw new Error(`Error ${res.status}`);
      const data = await res.json();
      setCreated(data);
    } catch (err) {
      setApiError(`// ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const reset = () => {
    setForm({ name: "", email: "" });
    setCreated(null);
    setApiError("");
    setErrors({});
  };

  return (
    <>
      <style>{styles}</style>
      <div className="cu-root">
        <div className="cu-bg-grid" />
        <div className="cu-bg-glow" />
        <div className="cu-card">

          {!created ? (
            <>
              <div className="cu-ticker">TRADING PLATFORM // NEW ACCOUNT</div>
              <h1 className="cu-title">Create<br /><span>Account</span></h1>
              <p className="cu-subtitle">Register to start trading. Initial balance ₹1,00,000.</p>

              <div className="cu-field">
                <label className="cu-label">Username</label>
                <div className="cu-input-wrap">
                  <input
                    className={`cu-input${errors.name ? " error" : ""}`}
                    placeholder="e.g. nikhil"
                    value={form.name}
                    onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
                    onKeyDown={e => e.key === "Enter" && handleSubmit()}
                  />
                </div>
                {errors.name && <div className="cu-error">{errors.name}</div>}
              </div>

              <div className="cu-field">
                <label className="cu-label">Email</label>
                <div className="cu-input-wrap">
                  <input
                    className={`cu-input${errors.email ? " error" : ""}`}
                    placeholder="nikhil@example.com"
                    type="email"
                    value={form.email}
                    onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                    onKeyDown={e => e.key === "Enter" && handleSubmit()}
                  />
                </div>
                {errors.email && <div className="cu-error">{errors.email}</div>}
              </div>

              <div className="cu-balance-info">
                <span className="cu-balance-label">Starting balance</span>
                <span className="cu-balance-value">₹1,00,000.00</span>
              </div>

              {apiError && <div className="cu-error" style={{ marginBottom: 16 }}>{apiError}</div>}

              <button className="cu-btn" onClick={handleSubmit} disabled={loading}>
                {loading ? (
                  <span className="cu-btn-loading">
                    <span className="cu-spinner" /> Creating account...
                  </span>
                ) : "Create Account →"}
              </button>
            </>
          ) : (
            <div className="cu-success">
              <div className="cu-ticker" style={{ justifyContent: "center" }}>ACCOUNT CREATED // SUCCESS</div>
              <div className="cu-success-icon">✓</div>
              <div className="cu-success-title">Welcome, {created.name}!</div>
              <div className="cu-success-msg">Your trading account is active.</div>

              <div className="cu-user-card">
                <div className="cu-user-row">
                  <span className="cu-user-key">User ID</span>
                  <span className="cu-user-val">#{created.id}</span>
                </div>
                <div className="cu-user-row">
                  <span className="cu-user-key">Name</span>
                  <span className="cu-user-val">{created.name}</span>
                </div>
                <div className="cu-user-row">
                  <span className="cu-user-key">Email</span>
                  <span className="cu-user-val">{created.email}</span>
                </div>
                <div className="cu-user-row">
                  <span className="cu-user-key">Balance</span>
                  <span className="cu-user-val green">₹{Number(created.balance).toLocaleString("en-IN")}</span>
                </div>
              </div>

              <button className="cu-reset-btn" onClick={reset}>
                + create another account
              </button>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
