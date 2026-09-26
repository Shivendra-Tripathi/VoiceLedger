import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserPen, Camera, Check, AlertTriangle, ArrowLeft } from 'lucide-react';
import AppShell from '../components/layout/AppShell';
import Button from '../components/common/Button';
import Avatar from '../components/common/Avatar';
import { useAuth } from '../context/AuthContext';
import { updateUserProfile } from '../api/userService';

/**
 * UpdateProfilePage
 * -----------------
 * Dedicated page for the shopkeeper to update their profile name and photo.
 * Submits multipart/form-data to PUT /users/me.
 */
export default function UpdateProfilePage() {
  const navigate = useNavigate();
  const { user, updateUser } = useAuth();

  const [name, setName] = useState(user?.name ?? '');
  const [imageFile, setImageFile] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formError, setFormError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  // Sync state if user loads after mount
  useEffect(() => {
    if (user?.name && !name) {
      setName(user.name);
    }
  }, [user?.name, name]);

  const handleImageChange = (event) => {
    const file = event.target.files?.[0];
    if (!file) return;

    setImageFile(file);
    setImagePreview(URL.createObjectURL(file));
    setSuccessMessage(null);
    setFormError(null);
  };

  // Revoke object URL on cleanup
  useEffect(() => {
    return () => {
      if (imagePreview) {
        URL.revokeObjectURL(imagePreview);
      }
    };
  }, [imagePreview]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setFormError(null);
    setSuccessMessage(null);

    if (!name.trim()) {
      setFormError('Your name cannot be empty.');
      return;
    }

    setIsSubmitting(true);

    try {
      const updatedUser = await updateUserProfile({
        name: name.trim(),
        image: imageFile,
      });

      // Sync across application
      updateUser(updatedUser);
      setSuccessMessage('Profile updated successfully!');
      setImageFile(null);
    } catch (err) {
      setFormError(
        err?.response?.data?.message ||
          'Could not update profile. Please verify your connection and try again.'
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AppShell>
      <div className="max-w-lg mx-auto px-4 sm:px-8 py-10">
        {/* Back navigation */}
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-1.5 text-sm text-ink-soft hover:text-maroon mb-6 font-body"
        >
          <ArrowLeft size={15} />
          Back
        </button>

        {/* Page Header */}
        <div className="flex items-center gap-3 mb-6">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-b from-brass-light to-brass flex items-center justify-center shadow-raised-sm border border-brass-dark/40">
            <UserPen size={19} className="text-maroon-dark" />
          </div>

          <div>
            <h1 className="font-display text-xl font-semibold text-ink">
              Update Profile
            </h1>
            <p className="text-xs text-ink-soft">
              Change your shopkeeper name and profile photo
            </p>
          </div>
        </div>

        {/* Form Card */}
        <form onSubmit={handleSubmit} className="surface-card p-6 sm:p-8 space-y-6">
          {/* Profile Photo Upload */}
          <div className="flex flex-col items-center gap-2.5">
            <label
              htmlFor="shopkeeper-photo"
              className="group relative w-24 h-24 rounded-full border-2 border-brass bg-paper flex items-center justify-center cursor-pointer overflow-hidden shadow-raised hover:border-brass-dark transition-all"
              title="Click to change profile photo"
            >
              {imagePreview ? (
                <img
                  src={imagePreview}
                  alt="New profile preview"
                  className="w-full h-full object-cover"
                />
              ) : user?.photoUrl ? (
                <img
                  src={user.photoUrl}
                  alt={user.name || 'Shopkeeper photo'}
                  className="w-full h-full object-cover"
                />
              ) : (
                <Avatar name={name || 'Shopkeeper'} size="md" />
              )}

              {/* Camera Hover Overlay */}
              <div className="absolute inset-0 bg-black/40 flex flex-col items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity text-white">
                <Camera size={20} strokeWidth={2} />
                <span className="text-[10px] mt-0.5 font-medium">Change</span>
              </div>

              <input
                id="shopkeeper-photo"
                type="file"
                accept="image/*"
                onChange={handleImageChange}
                className="hidden"
              />
            </label>

            <span className="text-xs text-ink-soft font-body">
              Click photo to select a new image
            </span>
          </div>

          {/* Shopkeeper Name Input */}
          <div>
            <label
              htmlFor="shopkeeper-name"
              className="block text-sm font-semibold text-ink mb-1.5"
            >
              Shopkeeper name
            </label>
            <input
              id="shopkeeper-name"
              type="text"
              value={name}
              onChange={(e) => {
                setName(e.target.value);
                setSuccessMessage(null);
              }}
              placeholder="e.g. Shivendra"
              className="w-full rounded-xl surface-pressed px-4 py-3 font-body text-sm text-ink placeholder:text-ink-soft/50 outline-none focus:ring-2 focus:ring-brass"
            />
          </div>

          {/* Feedback messages */}
          {successMessage && (
            <div className="flex items-center gap-2 p-3 rounded-xl bg-credit-soft text-credit text-sm font-medium">
              <Check size={16} strokeWidth={2.5} />
              <span>{successMessage}</span>
            </div>
          )}

          {formError && (
            <div className="flex items-center gap-2 p-3 rounded-xl bg-debit-soft text-debit text-sm font-medium">
              <AlertTriangle size={16} />
              <span>{formError}</span>
            </div>
          )}

          {/* Action Buttons */}
          <div className="flex items-center gap-3 pt-2">
            <Button
              type="submit"
              variant="brass"
              disabled={isSubmitting}
              className="flex-1"
            >
              {isSubmitting ? 'Saving changes…' : 'Save changes'}
            </Button>

            <Button
              type="button"
              variant="ghost"
              onClick={() => navigate(-1)}
              disabled={isSubmitting}
            >
              Cancel
            </Button>
          </div>
        </form>
      </div>
    </AppShell>
  );
}

