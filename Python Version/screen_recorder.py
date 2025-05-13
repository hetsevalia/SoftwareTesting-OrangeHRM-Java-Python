import cv2
import numpy as np
import pyautogui
import threading
import time
import os

class ScreenRecorder:
    def __init__(self, filename="recordings/orangehrm_flow.avi", fps=10):
        os.makedirs("recordings", exist_ok=True)
        self.filename = filename
        self.fps = fps
        self.recording = False
        self.thread = None

    def start(self):
        self.recording = True
        self.thread = threading.Thread(target=self._record)
        self.thread.start()

    def stop(self):
        self.recording = False
        self.thread.join()

    def _record(self):
        screen_size = pyautogui.size()
        fourcc = cv2.VideoWriter_fourcc(*"XVID")
        out = cv2.VideoWriter(self.filename, fourcc, self.fps, screen_size)

        while self.recording:
            img = pyautogui.screenshot()
            frame = np.array(img)
            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            out.write(frame)
            time.sleep(1 / self.fps)

        out.release()
