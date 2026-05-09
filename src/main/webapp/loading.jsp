<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Loading NUSTcord...</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            background-color: #111113;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: #f2f3f5;
        }
        .loading-container {
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        .logo {
            font-size: 2.5rem;
            font-weight: bold;
            margin-bottom: 20px;
            letter-spacing: 2px;
            text-shadow: 0 0 10px rgba(114, 137, 218, 0.5);
            animation: pulse 1.5s infinite;
        }
        .progress-bar-bg {
            width: 300px;
            height: 6px;
            background-color: #1e1e24;
            border-radius: 3px;
            overflow: hidden;
        }
        .progress-bar {
            width: 0;
            height: 100%;
            background-color: #7289da;
            animation: load 2.5s cubic-bezier(0.4, 0.0, 0.2, 1) forwards;
        }
        @keyframes load {
            0% { width: 0; }
            50% { width: 60%; }
            100% { width: 100%; }
        }
        @keyframes pulse {
            0% { opacity: 0.7; transform: scale(0.98); }
            50% { opacity: 1; transform: scale(1); }
            100% { opacity: 0.7; transform: scale(0.98); }
        }
    </style>
    <script>
        setTimeout(function() {
            window.location.href = "dashboard.jsp";
        }, 2800); // Redirect after animation completes (approx 2.8s)
    </script>
</head>
<body>
    <div class="loading-container">
        <div class="logo">NUSTcord</div>
        <div class="progress-bar-bg">
            <div class="progress-bar"></div>
        </div>
    </div>
</body>
</html>

