from fastapi import FastAPI, Form
from fastapi.responses import JSONResponse, HTMLResponse
import threading
import cv2
import subprocess
import os

app = FastAPI()

video_path = None
cap = None
is_playing = False
lock = threading.Lock()

def video_loop():
    global cap, is_playing
    while is_playing and cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            break
        cv2.imshow("Video Player", frame)
        fps = cap.get(cv2.CAP_PROP_FPS)
        delay = int(1000 / fps) if fps > 0 else 30
        if cv2.waitKey(delay) & 0xFF == ord('q'):
            break
    is_playing = False
    if cap:
        cap.release()
        cv2.destroyAllWindows()

@app.post("/load")
def load_video(path: str = Form(...)):
    global video_path, cap, is_playing
    with lock:
        if cap:
            cap.release()
        cap = cv2.VideoCapture(path)
        video_path = path
        is_playing = False
    return {"status": "loaded"}

@app.post("/play")
def play_video():
    global is_playing
    with lock:
        if not is_playing:
            is_playing = True
            threading.Thread(target=video_loop, daemon=True).start()
    return {"status": "playing"}

@app.post("/pause")
def pause_video():
    global is_playing
    with lock:
        is_playing = False
    return {"status": "paused"}

@app.get("/timestamp")
def get_timestamp():
    with lock:
        if cap and cap.isOpened():
            ts = int(cap.get(cv2.CAP_PROP_POS_MSEC))
            return JSONResponse({"timestamp_ms": ts})
    return JSONResponse({"timestamp_ms": 0})

# === HTML video player endpoint ===
@app.get("/video")
def serve_video_page():
    if not video_path or not os.path.isfile(video_path):
        return HTMLResponse("<h2>No video loaded</h2>")
    
    filename = os.path.basename(video_path)
    html = f"""
    <html>
    <head><title>Video Player</title></head>
    <body style="margin:0;">
        <video width="1280" height="720" controls autoplay>
            <source src="/video_file/{filename}" type="video/mp4">
            Your browser does not support the video tag.
        </video>
    </body>
    </html>
    """
    return HTMLResponse(content=html)

from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles

# Mount the directory of the video as static for serving in the browser
video_dir = os.path.abspath(".")
app.mount("/video_file", StaticFiles(directory=video_dir), name="video_file")

# === NEW: /play_browser opens Chrome window in fixed size ===
@app.get("/play_browser")
def open_browser():
    url = "http://127.0.0.1:8000/video"
    chrome_path = r"C:\Program Files\Google\Chrome\Application\chrome.exe"  # Adjust if different
    if not os.path.exists(chrome_path):
        return JSONResponse({"error": "Chrome not found. Check path."}, status_code=500)

    try:
        subprocess.Popen([
            chrome_path,
            url,
            '--new-window',
            '--window-size=1280,720',
            '--window-position=100,100',
            '--start-maximized=false'
        ])
        return {"status": "browser opened"}
    except Exception as e:
        return JSONResponse({"error": str(e)}, status_code=500)
