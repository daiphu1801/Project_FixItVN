# Huong dan xu ly Git conflict khi pull --rebase

Tai lieu nay dung cho truong hop ban chay:

```powershell
git pull --rebase origin main
```

va Git bao conflict, vi du:

```text
CONFLICT (modify/delete): path/to/file deleted in HEAD and modified in <commit>.
error: could not apply <commit>...
```

## 1. Hieu loi dang gap

`pull --rebase` co nghia la:

1. Git lay code moi nhat tu remote `origin/main`.
2. Git tam thoi nhac commit local cua ban ra.
3. Git dat branch local len tren remote moi nhat.
4. Git apply lai tung commit local cua ban len dau remote.

Conflict xay ra khi remote va local cung cham vao mot file theo hai cach khac nhau.

Vi du `modify/delete`:

- Remote da xoa file.
- Commit local cua ban lai sua file do.
- Git khong tu quyet dinh duoc nen giu file hay xoa file.

## 2. Kiem tra trang thai

Chay:

```powershell
git status
```

Hoac ban ngan gon:

```powershell
git status --short
```

File conflict thuong hien cac trang thai nhu:

```text
DU path/to/file
UD path/to/file
UU path/to/file
AA path/to/file
```

Y nghia nhanh:

- `DU`: deleted by us, file bi xoa o phia hien tai, nhung commit dang apply co sua file.
- `UD`: deleted by them, phia remote/commit kia xoa file.
- `UU`: ca hai ben cung sua file.
- `AA`: ca hai ben cung them file cung ten.

## 3. Xem file dang duoc tham chieu o dau

Truoc khi quyet dinh giu hay xoa file, hay tim file do co con duoc code dung khong.

Vi du conflict voi layout:

```powershell
rg -n "fragment_worker_chat|FragmentWorkerChatBinding|workerChatFragment" android/app/src/main
```

Neu file la Java class:

```powershell
rg -n "TenClass|tenMethod|tenFile" android/app/src/main/java
```

Muc tieu la tra loi 2 cau hoi:

- File nay co con duoc import/reference khong?
- Remote co file thay the moi khong?

## 4. Cach resolve theo tung truong hop

### Truong hop A: Muon giu file local

Dung khi local sua file van can thiet, remote xoa nham hoac file van duoc code tham chieu.

```powershell
git add path/to/file
git rebase --continue
```

Neu file bi Git danh dau deleted va ban can lay lai noi dung local dang nam trong working tree, kiem tra file truoc:

```powershell
Get-Content path/to/file
git add path/to/file
git rebase --continue
```

### Truong hop B: Muon theo remote, xoa file

Dung khi remote da thay file bang file moi, hoac file cu khong con duoc dung.

```powershell
git rm path/to/file
git rebase --continue
```

Neu `git rm` bi loi vi file da bi xoa khoi working tree:

```powershell
git add -u path/to/file
git rebase --continue
```

### Truong hop C: Giu file moi cua remote, sua code local de dung file moi

Day la truong hop vua gap:

- Remote xoa `fragment_worker_chat.xml`.
- Remote co layout moi `fragment_chat_worker.xml`.
- Code local van import `FragmentWorkerChatBinding`.
- Cach dung la xoa layout cu va doi Java sang binding moi.

Quy trinh:

```powershell
rg -n "FragmentWorkerChatBinding|fragment_worker_chat|fragment_chat_worker" android/app/src/main
```

Sua code Java:

```java
// Cu
import com.fixit.databinding.FragmentWorkerChatBinding;

// Moi
import com.fixit.databinding.FragmentChatWorkerBinding;
```

Sau do:

```powershell
git rm android/app/src/main/res/layout/fragment_worker_chat.xml
git add android/app/src/main/java/com/fixit/feature/worker/chat/presentation/WorkerChatFragment.java
git rebase --continue
```

## 5. Xu ly conflict noi dung trong cung mot file

Neu file co marker:

```text
<<<<<<< HEAD
noi dung remote
=======
noi dung local
>>>>>>> commit
```

Lam theo buoc:

1. Mo file.
2. Chon noi dung dung, hoac ket hop ca hai.
3. Xoa toan bo marker `<<<<<<<`, `=======`, `>>>>>>>`.
4. Kiem tra file con syntax dung.
5. Chay:

```powershell
git add path/to/file
git rebase --continue
```

## 6. Kiem tra con conflict khong

```powershell
git diff --name-only --diff-filter=U
```

Neu lenh khong in ra gi, khong con unmerged file.

Kiem tra trang thai:

```powershell
git status
```

Neu Git van bao:

```text
You are currently rebasing...
```

thi tiep tuc:

```powershell
git rebase --continue
```

## 7. Neu lam sai thi quay lai

Neu dang rebase va thay resolve sai huong, dung:

```powershell
git rebase --abort
```

Lenh nay dua repo ve trang thai truoc khi chay `git pull --rebase`.

Chi dung khi ban muon huy toan bo qua trinh rebase hien tai.

## 8. Build sau khi resolve

Sau khi rebase xong:

```powershell
cd android
.\gradlew.bat --no-daemon :app:assembleDebug
```

Neu build thanh cong, quay ve root repo:

```powershell
cd ..
git status
```

Neu clean hoac chi co thay doi ban chu dong tao them, push:

```powershell
git push origin main
```

## 9. Checklist nhanh

1. `git status`
2. Doc loai conflict.
3. `rg` de xem file co con duoc reference khong.
4. Chon:
   - `git add file` de giu file.
   - `git rm file` de xoa file.
   - sua code neu remote co file thay the.
5. `git diff --name-only --diff-filter=U`
6. `git rebase --continue`
7. Build Android.
8. `git push origin main`

## 10. Nguyen tac an toan

- Khong dung `git push --force` tren `main` neu chua chac chan.
- Khong dung `git reset --hard` khi dang conflict neu chua backup.
- Luon doc `git status` truoc khi `git add .`.
- Voi conflict layout Android, luon search binding class tuong ung vi ViewBinding sinh class theo ten XML.
