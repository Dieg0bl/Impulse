import React from 'react';

export const EvidencePage: React.FC = () => {
  return (
    <div className="evidence-page">
      <div className="container">
        <div className="page-header">
          <h1>Evidence Management</h1>
          <p>Upload and manage evidence for your challenges</p>
        </div>
        
        <div className="evidence-filters">
          <div className="filter-tabs">
            <button className="tab active">All Evidence</button>
            <button className="tab">Pending Review</button>
            <button className="tab">Approved</button>
            <button className="tab">Rejected</button>
          </div>
          
          <div className="filter-controls">
            <select className="filter-select">
              <option value="">All Challenges</option>
              <option value="photo">Photo Verification</option>
              <option value="location">Location Check</option>
              <option value="identity">Identity Verification</option>
            </select>
            
            <input 
              type="text" 
              placeholder="Search evidence..." 
              className="search-input"
            />
          </div>
        </div>
        
        <div className="evidence-grid">
          <div className="evidence-card">
            <div className="evidence-header">
              <h3>Photo Verification</h3>
              <span className="status pending">Pending</span>
            </div>
            <div className="evidence-content">
              <div className="evidence-file">
                <img src="/sample-photo.jpg" alt="Evidence" />
              </div>
              <div className="evidence-details">
                <p><strong>Type:</strong> Photo</p>
                <p><strong>Uploaded:</strong> 2 hours ago</p>
                <p><strong>Size:</strong> 2.3 MB</p>
                <p><strong>Status:</strong> Under review</p>
              </div>
            </div>
            <div className="evidence-actions">
              <button className="btn btn-sm btn-outline">View</button>
              <button className="btn btn-sm btn-outline">Download</button>
              <button className="btn btn-sm btn-danger">Delete</button>
            </div>
          </div>
          
          <div className="evidence-card">
            <div className="evidence-header">
              <h3>Location Verification</h3>
              <span className="status approved">Approved</span>
            </div>
            <div className="evidence-content">
              <div className="evidence-file document">
                <div className="file-icon">📄</div>
                <span>location-proof.pdf</span>
              </div>
              <div className="evidence-details">
                <p><strong>Type:</strong> Document</p>
                <p><strong>Uploaded:</strong> 1 day ago</p>
                <p><strong>Size:</strong> 1.8 MB</p>
                <p><strong>Status:</strong> Approved</p>
              </div>
            </div>
            <div className="evidence-actions">
              <button className="btn btn-sm btn-outline">View</button>
              <button className="btn btn-sm btn-outline">Download</button>
              <button className="btn btn-sm btn-danger">Delete</button>
            </div>
          </div>
          
          <div className="evidence-card">
            <div className="evidence-header">
              <h3>Identity Check</h3>
              <span className="status rejected">Rejected</span>
            </div>
            <div className="evidence-content">
              <div className="evidence-file">
                <img src="/id-document.jpg" alt="Evidence" />
              </div>
              <div className="evidence-details">
                <p><strong>Type:</strong> Photo</p>
                <p><strong>Uploaded:</strong> 3 days ago</p>
                <p><strong>Size:</strong> 1.5 MB</p>
                <p><strong>Status:</strong> Rejected - unclear image</p>
              </div>
            </div>
            <div className="evidence-actions">
              <button className="btn btn-sm btn-outline">View</button>
              <button className="btn btn-sm btn-primary">Re-upload</button>
              <button className="btn btn-sm btn-danger">Delete</button>
            </div>
          </div>
        </div>
        
        <div className="upload-section">
          <div className="upload-area">
            <div className="upload-icon">📤</div>
            <h3>Upload New Evidence</h3>
            <p>Drag and drop files here or click to browse</p>
            <button className="btn btn-primary">Choose Files</button>
          </div>
          
          <div className="upload-guidelines">
            <h4>Upload Guidelines</h4>
            <ul>
              <li>Maximum file size: 10MB</li>
              <li>Supported formats: JPG, PNG, PDF, DOC</li>
              <li>Ensure images are clear and readable</li>
              <li>Include all required documentation</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};
